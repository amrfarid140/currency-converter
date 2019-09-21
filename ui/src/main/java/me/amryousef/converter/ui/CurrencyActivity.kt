package me.amryousef.converter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import me.amryousef.converter.presentation.CurrencyRatesViewModel
import me.amryousef.converter.presentation.ViewState
import me.amryousef.converter.presentation.ViewStateItem
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_currency.activity_currency_error_message as errorMessage
import kotlinx.android.synthetic.main.activity_currency.activity_currency_list as list
import kotlinx.android.synthetic.main.activity_currency.activity_currency_progress as progress
import kotlinx.android.synthetic.main.activity_currency.activity_currency_retry_button as retryButton

class CurrencyActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CurrencyRatesViewModel::class.java)
    }
    private val valueTextWatcher by lazy {
        ValueTextWatcher(viewModel)
    }

    private val listAdapter = CurrencyListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)
        retryButton.setOnClickListener {
            viewModel.onRetryClicked()
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = listAdapter
        viewModel.state.observe(this, Observer { handleState(it) })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onViewStarted()
    }

    override fun onPause() {
        viewModel.onViewPaused()
        super.onPause()
    }

    private fun handleState(state: ViewState) = when (state) {
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
            state.items.firstOrNull()?.currencyCode?.let {
                valueTextWatcher.setCurrencyCode(it)
            }
            listAdapter.submitList(state.items.toViewData())
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
                viewModel.onRowFocused(item.currencyCode)
            },
            textWatcher = valueTextWatcher,
            isFocused = index == 0 && valueTextWatcher.currencyCode == item.currencyCode
        )
    }
}