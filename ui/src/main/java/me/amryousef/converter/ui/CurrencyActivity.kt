package me.amryousef.converter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import me.amryousef.converter.presentation.CurrencyRatesPresenter
import me.amryousef.converter.presentation.CurrencyRatesView
import me.amryousef.converter.presentation.ViewState
import me.amryousef.converter.presentation.ViewStateItem
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_currency.activity_currency_error_message as errorMessage
import kotlinx.android.synthetic.main.activity_currency.activity_currency_list as list
import kotlinx.android.synthetic.main.activity_currency.activity_currency_progress as progress
import kotlinx.android.synthetic.main.activity_currency.activity_currency_retry_button as retryButton

class CurrencyActivity : AppCompatActivity(), CurrencyRatesView {

    @Inject
    lateinit var presenter: CurrencyRatesPresenter

    private val valueTextWatcher by lazy {
        ValueTextWatcher(presenter)
    }

    private val listAdapter = CurrencyListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(presenter)
        setContentView(R.layout.activity_currency)
        retryButton.setOnClickListener {
            presenter.retryFetchingData()
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = listAdapter
        presenter.bindView(this)
    }

    override fun handleState(viewState: ViewState) = when (viewState) {
        is ViewState.Loading -> {
            progress.isVisible = true
            list.isVisible = false
            errorMessage.isVisible = false
            retryButton.isVisible = false
        }

        is ViewState.Ready -> {
            progress.isVisible = false
            list.isVisible = true
            errorMessage.isVisible = false
            retryButton.isVisible = false
            viewState.items.firstOrNull()?.currencyCode?.let {
                valueTextWatcher.setCurrencyCode(it)
            }
            listAdapter.submitList(viewState.items.toViewData())
        }

        is ViewState.Error -> {
            progress.isVisible = false
            list.isVisible = false
            errorMessage.isVisible = true
            retryButton.isVisible = true
        }
    }

    private fun List<ViewStateItem>.toViewData() = mapIndexed { index, item ->
        CurrencyRowViewData(
            countryFlagUrl = item.countryFlagUrl,
            currencyCode = item.currencyCode,
            value = item.value,
            onEditTextFocused = {
                list.scrollToPosition(0)
                presenter.onRowFocused(item.currencyCode)
            },
            textWatcher = valueTextWatcher,
            isFocused = index == 0 && valueTextWatcher.currencyCode == item.currencyCode
        )
    }

    override fun onDestroy() {
        lifecycle.removeObserver(presenter)
        super.onDestroy()
    }
}