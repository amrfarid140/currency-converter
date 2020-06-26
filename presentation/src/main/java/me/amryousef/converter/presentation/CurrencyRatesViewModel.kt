package me.amryousef.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import javax.inject.Inject

class CurrencyRatesViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>().apply {
        value = ViewState.Loading
    }
    val state: LiveData<ViewState> = _state
    private val originalRates = mutableListOf<ViewStateItem>()

    fun onViewStarted() {
        loadData()
    }

    fun onViewPaused() {
        fetchDataUseCase.cancel()
    }

    fun onRetryClicked() {
        fetchDataUseCase.cancel()
        _state.value = ViewState.Loading
        loadData()
    }

    private fun loadData() {
        fetchDataUseCase.execute { result -> result.reduce() }
    }

    private fun UseCaseResult<List<CurrencyData>>.reduce() =
        when (this) {
            is UseCaseResult.Success -> {
                val items = data.toStateItem()
                originalRates.clear()
                originalRates.addAll(items)
                if (items.isNotEmpty()) {
                    setReadyState(items)
                } else {
                    _state.value = ViewState.Loading
                }
            }

            is UseCaseResult.Error -> _state.value = ViewState.Error
        }

    private fun UseCaseResult<List<CurrencyData>>.setReadyState(items: List<ViewStateItem>) =
        (_state.value as? ViewState.Ready)?.let { currentState ->
            val focusedItem = currentState.items.first()
            onRowValueChanged(focusedItem.currencyCode, focusedItem.value)
        } ?: run {
            _state.value = ViewState.Ready(items)
        }

    fun onRowFocused(currencyCode: String) =
        computeStateFromReady {
            ViewState.Ready(
                mutableSetOf<ViewStateItem>().apply {
                    items.find { it.currencyCode == currencyCode }?.let { add(it) }
                    addAll(items)
                }.toList()
            )
        }

    fun onRowValueChanged(currencyCode: String, newValueText: String) =
        newValueText.toDoubleOrNull()?.let { newValue ->
            computeStateFromReady {
                val currentStateItems = items.filter { it.currencyCode != currencyCode }
                val focusedItem = items.find { it.currencyCode == currencyCode }
                val newStateItems = focusedItem.updateOriginalRates(
                    currencyCode,
                    newValue,
                    currentStateItems
                )
                ViewState.Ready(
                    mutableListOf<ViewStateItem>().apply {
                        focusedItem?.let { item -> add(item.copy(value = newValueText)) }
                        addAll(newStateItems)
                    }
                )
            }
        }

    private fun ViewStateItem?.updateOriginalRates(
        currencyCode: String,
        newValue: Double,
        currentStateItems: List<ViewStateItem>) =
        if (this?.isBase == true) {
            updateRates(currentStateItems, newValue)
        } else {
            val originalRate = originalRates.find { it.currencyCode == currencyCode }
            val newBaseValue =
                originalRate?.let { safeRate -> safeRate.value.toDoubleOrNull()?.let { newValue / it } }
                    ?: 0.0
            updateRates(currentStateItems, newBaseValue)
        }

    private fun computeStateFromReady(block: ViewState.Ready.() -> ViewState.Ready) =
        (_state.value as? ViewState.Ready)?.let { currentState ->
            _state.value = currentState.block()
            true
        } ?: false

    private fun List<CurrencyData>.toStateItem() = map { rate ->
        ViewStateItem(
            rate.countryFlagUrl,
            rate.currency.currencyCode,
            rate.rate.formatValue(),
            rate.isBase
        )
    }

    private fun updateRates(currentStateItems: List<ViewStateItem>, newValue: Double) =
        currentStateItems.map { currentValue ->
            val originalRate = originalRates.find { it.currencyCode == currentValue.currencyCode }
                ?: currentValue
            if (originalRate.isBase) {
                originalRate.copy(value = newValue.formatValue())
            } else {
                originalRate.value.toDoubleOrNull()?.let {
                    originalRate.copy(value = (newValue * it).formatValue())
                } ?: originalRate
            }
        }

    private fun Double.formatValue() =
        String.format("%.2f", this)

    override fun onCleared() {
        fetchDataUseCase.cancel()
    }
}