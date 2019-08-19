package me.amryousef.converter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import javax.inject.Inject

class CurrencyRatesViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState> = _state
    private val originalRates = mutableListOf<ViewStateItem>()

    init {
        loadData()
    }

    private fun loadData() {
        _state.value = ViewState.Loading
        fetchDataUseCase.execute { result -> result.reduce() }
    }

    private fun UseCaseResult<List<CurrencyRate>>.reduce() =
        when (this) {
            is UseCaseResult.Success -> {
                val items = data.toStateItem()
                originalRates.clear()
                originalRates.addAll(items)
                (_state.value as? ViewState.Ready)?.let { currentState ->
                    val focusedItem = currentState.items.first()
                    onRowValueChanged(focusedItem.currencyCode, focusedItem.value)
                } ?: run {
                    _state.value = ViewState.Ready(items)
                }
            }

            is UseCaseResult.Error -> _state.value = ViewState.Error
        }

    private fun List<CurrencyRate>.toStateItem() = map { rate ->
        ViewStateItem(rate.currency.currencyCode, rate.rate.formatValue(), rate.isBase)
    }

    fun onRetryClicked() {
        fetchDataUseCase.cancel()
        loadData()
    }

    fun onRowFocused(currencyCode: String) =
        (_state.value as? ViewState.Ready)?.let { currentState ->
            _state.value = ViewState.Ready(
                mutableSetOf<ViewStateItem>().apply {
                    currentState.items.find { it.currencyCode == currencyCode }?.let { add(it) }
                    addAll(
                        currentState.items
                    )

                }.toList()
            )
        } ?: Unit

    fun onRowValueChanged(currencyCode: String, newValueText: String) =
        newValueText.toDoubleOrNull()?.let { newValue ->
            (state.value as? ViewState.Ready)?.let { currentState ->
                val currentStateItems = currentState.items.filter { it.currencyCode != currencyCode }
                val focusedItem = currentState.items.find { it.currencyCode == currencyCode }
                val newStateItems = if (focusedItem?.isBase == true) {
                    updateRates(currentStateItems, newValue)
                } else {
                    val originalRate = originalRates.find { it.currencyCode == currencyCode }
                    val newBaseValue =
                        originalRate?.let { safeRate -> safeRate.value.toDoubleOrNull()?.let { newValue / it } }
                            ?: 0.0
                    updateRates(currentStateItems, newBaseValue)
                }
                _state.value = ViewState.Ready(
                    mutableListOf<ViewStateItem>().apply {
                        focusedItem?.let { item -> add(item.copy(value = newValueText)) }
                        addAll(newStateItems)
                    }
                )
            }
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