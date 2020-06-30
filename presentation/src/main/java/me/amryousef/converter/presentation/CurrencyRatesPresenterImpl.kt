package me.amryousef.converter.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CurrencyRatesPresenterImpl @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase
) : CurrencyRatesPresenter, CoroutineScope, Observer<ViewState> {

    private var fetchDataJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate

    private val originalRates = mutableListOf<ViewStateItem>()

    private lateinit var weakView: WeakReference<CurrencyRatesView>

    private val state = MutableLiveData<ViewState>()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.i("Starting data fetch")
        state.observeForever(this)
        observeUseCase()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.i("Stopping data fetch")
        state.removeObserver(this)
        fetchDataJob?.cancel()
    }

    override fun bindView(view: CurrencyRatesView) {
        weakView = WeakReference(view)
    }

    override fun onRowFocused(currencyCode: String) {
        state.value = computeStateFromReady {
            ViewState.Ready(
                mutableSetOf<ViewStateItem>().apply {
                    items.find { it.currencyCode == currencyCode }?.let { add(it) }
                    addAll(items)
                }.toList()
            )
        }
    }

    override fun onRowValueChanged(currencyCode: String, newValueText: String) {
        state.value = computeStateWithCurrencyAndValue(currencyCode, newValueText)
    }

    override fun retryFetchingData() {
        fetchDataJob?.cancel()
        observeUseCase()
    }

    private fun observeUseCase() {
        fetchDataJob = launch {
            fetchDataUseCase.execute()
                .collect {
                    state.value = it.reduce()
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

    private fun List<CurrencyData>.toStateItem() = map { rate ->
        ViewStateItem(
            rate.countryFlagUrl,
            rate.currency.currencyCode,
            rate.rate.formatValue(),
            rate.isBase
        )
    }

    private fun UseCaseResult<List<CurrencyData>>.setReadyState(items: List<ViewStateItem>) =
        (state.value as? ViewState.Ready)?.let { currentState ->
            val focusedItem = currentState.items.first()
            computeStateWithCurrencyAndValue(focusedItem.currencyCode, focusedItem.value)
        } ?: run {
            ViewState.Ready(items)
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
        currentStateItems: List<ViewStateItem>
    ) =
        if (this?.isBase == true) {
            updateRates(currentStateItems, newValue)
        } else {
            val originalRate = originalRates.find { it.currencyCode == currencyCode }
            val newBaseValue =
                originalRate?.let { safeRate ->
                    safeRate.value.toDoubleOrNull()?.let { newValue / it }
                }
                    ?: 0.0
            updateRates(currentStateItems, newBaseValue)
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

    private fun computeStateFromReady(block: ViewState.Ready.() -> ViewState.Ready) =
        (state.value as? ViewState.Ready)?.let { currentState ->
            currentState.block()
        } ?: state.value

    override fun onChanged(viewState: ViewState?) {
        viewState?.let { weakView.get()?.handleState(it) }
    }
}