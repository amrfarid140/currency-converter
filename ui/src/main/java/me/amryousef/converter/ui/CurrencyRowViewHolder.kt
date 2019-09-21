package me.amryousef.converter.ui

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.Currency
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_code as currencyCode
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_name as currencyName
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_value_input_field as valueInputField
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_country_flag as countryFlag

class CurrencyRowViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.row_currency_item, parent, false)
) {
    fun bind(item: CurrencyRowViewData) = with(itemView) {
        if (item.isFocused) {
            Log.v(this.javaClass.simpleName, "currency ${item.currencyCode}")
            Log.v(this@CurrencyRowViewHolder.javaClass.simpleName, "currency ${item.value}")
        }
        setupFlag(item)
        setupLabels(item)
        setupInput(item)

    }

    private fun View.setupFlag(item: CurrencyRowViewData) {
        countryFlag.isVisible = !item.countryFlagUrl.isNullOrEmpty()
        item.countryFlagUrl?.let { url ->
            Picasso.get().load(url).into(countryFlag)
        }
    }

    private fun View.setupLabels(item: CurrencyRowViewData) {
        currencyCode.text = item.currencyCode
        val currency = Currency.getInstance(item.currencyCode)
        currencyName.text = currency.displayName
    }

    private fun View.setupInput(item: CurrencyRowViewData) {
        valueInputField.setOnTouchListener(null)
        valueInputField.removeTextChangedListener(item.textWatcher)
        if (item.textWatcher.currencyCode != item.currencyCode || !valueInputField.hasFocus()) {
            setupNonFocusedItem(item)
        } else {
            setupFocusedItem(item)
        }
    }

    private fun View.setupNonFocusedItem(item: CurrencyRowViewData) {
        valueInputField.text = Editable.Factory.getInstance().newEditable(item.value)
        valueInputField.clearFocus()
        valueInputField.setOnTouchListener { _, _ ->
            item.onEditTextFocused()
            false
        }
    }

    private fun View.setupFocusedItem(item: CurrencyRowViewData) {
        valueInputField.requestFocus()
        if (valueInputField.text?.isBlank() == true && item.textWatcher.oldText.isBlank()) {
            valueInputField.text = Editable.Factory.getInstance().newEditable(item.value)
        }
        valueInputField.addTextChangedListener(item.textWatcher)
    }
}