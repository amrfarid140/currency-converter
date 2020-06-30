package me.amryousef.converter.ui

import android.text.Editable
import android.text.TextWatcher
import me.amryousef.converter.presentation.CurrencyRatesPresenter

class ValueTextWatcher(private val presenter: CurrencyRatesPresenter) : TextWatcher {
    var currencyCode: String? = null
        private set
    var oldText: String = ""
        private set

    fun setCurrencyCode(code: String) {
        if (this.currencyCode != code) {
            this.currencyCode = code
            oldText = ""
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        oldText = s?.toString() ?: ""
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        currencyCode?.let { currency ->
            s?.toString()?.let { value ->
                presenter.onRowValueChanged(currency, value)
            }
        }
    }
}