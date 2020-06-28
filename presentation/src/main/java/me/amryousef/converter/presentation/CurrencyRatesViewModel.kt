package me.amryousef.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import javax.inject.Inject

class CurrencyRatesViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase
) : ViewModel() {

    init {
        observeUseCase()
    }

    private var fetchDataJob: Job? = null
    private val _state = MutableLiveData<ViewState>().apply {
        value = ViewState.Loading
    }
    val state: LiveData<ViewState> = _state

    private val originalRates = mutableListOf<ViewStateItem>()

    private fun observeUseCase() {
        fetchDataJob = viewModelScope.launch {
            fetchDataUseCase.execute()
                .collect {
                    _state.value = it.reduce()
                }
        }
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
                    ViewState.Loading
                }
            }

            is UseCaseResult.Error -> ViewState.Error
        }

    private fun UseCaseResult<List<CurrencyData>>.setReadyState(items: List<ViewStateItem>) =
        (state.value as? ViewState.Ready)?.let { currentState ->
            val focusedItem = currentState.items.first()
            computeStateWithCurrencyAndValue(focusedItem.currencyCode, focusedItem.value)
        } ?: run {
            ViewState.Ready(items)
        }

    fun onRowFocused(currencyCode: String) {
        _state.value = computeStateFromReady {
            ViewState.Ready(
                mutableSetOf<ViewStateItem>().apply {
                    items.find { it.currencyCode == currencyCode }?.let { add(it) }
                    addAll(items)
                }.toList()
            )
        }
    }

    fun onRetryClicked() {
        fetchDataJob?.cancel()
        observeUseCase()
    }

    fun onRowValueChanged(currencyCode: String, newValueText: String) {
        _state.value = computeStateWithCurrencyAndValue(currencyCode, newValueText)
    }

    private fun computeStateWithCurrencyAndValue(currencyCode: String, newValueText: String) =
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
        (state.value as? ViewState.Ready)?.let { currentState ->
            currentState.block()
        } ?: state.value

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
        super.onCleared()
        fetchDataJob?.cancel()
    }
}