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
): ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState> = _state
    private val originalRates = mutableListOf<ViewStateItem>()

    init {
        loadData()
    }

    private fun loadData() {
        _state.value = ViewState.Error
        fetchDataUseCase.execute { result -> _state.value = result.reduce() }
    }

    private fun UseCaseResult<List<CurrencyRate>>.reduce() =
        when (this) {
            is UseCaseResult.Success -> {
                val items = data.toStateItem()
                originalRates.clear()
                originalRates.addAll(items)
                ViewState.Ready(items)
            }
            is UseCaseResult.Error -> ViewState.Error
        }

    private fun List<CurrencyRate>.toStateItem() = map { rate ->
        ViewStateItem(rate.currency.currencyCode, rate.rate, rate.isBase)
    }

    fun onRetryClicked() = loadData()

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

    fun onRowValueChanged(currencyCode: String, newValue: Double) =
        (state.value as? ViewState.Ready)?.let { currentState ->
            val focusedItem = currentState.items.find { it.currencyCode == currencyCode }
            if (focusedItem?.isBase == true) {
                updateRates(newValue)
            } else {
                val originalRate = originalRates.find { it.currencyCode == currencyCode }
                val newBaseValue =
                    originalRate?.let { safeRate -> newValue / safeRate.value } ?: 0.0
                updateRates(newBaseValue)
            }
        }

    private fun updateRates(newValue: Double) {
        _state.value = ViewState.Ready(
            originalRates.map { originalRate ->
                if (originalRate.isBase) {
                    originalRate.copy(value = newValue)
                } else {
                    originalRate.copy(value = newValue * originalRate.value)
                }
            }
        )
    }

    override fun onCleared() {
        fetchDataUseCase.cancel()
    }
}