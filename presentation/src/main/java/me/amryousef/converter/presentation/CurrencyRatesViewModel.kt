package me.amryousef.converter.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import javax.inject.Inject

class CurrencyRatesViewModel @Inject constructor(
    fetchDataUseCase: FetchDataUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>().apply {
        value = ViewState.Loading
    }
    val state: LiveData<ViewState> =
        fetchDataUseCase.execute(null)
            .map {
                it.reduce()
            }.onStart { emit(ViewState.Loading) }.asLiveData(viewModelScope.coroutineContext)

    private val originalRates = mutableListOf<ViewStateItem>()

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
        (_state.value as? ViewState.Ready)?.let { currentState ->
            val focusedItem = currentState.items.first()
            onRowValueChanged(focusedItem.currencyCode, focusedItem.value)
        } ?: run {
            ViewState.Ready(items)
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
            currentState.block()
        } ?: _state.value

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
}