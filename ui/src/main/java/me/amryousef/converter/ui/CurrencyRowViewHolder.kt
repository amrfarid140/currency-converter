package me.amryousef.converter.ui

import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.Currency
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_code as currencyCode
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_name as currencyName
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_value_input_field as valueInputField
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_value_input_layout as valueInputLayout

class CurrencyRowViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.row_currency_item, parent, false)
) {
    fun bind(item: CurrencyRowViewData) = with(itemView) {
        currencyCode.text = item.currencyCode
        val currency = Currency.getInstance(item.currencyCode)
        currencyName.text = currency.displayName
        valueInputField.setOnTouchListener(null)
        if (item.textWatcher.currencyCode != item.currencyCode) {
            valueInputField.removeTextChangedListener(item.textWatcher)
            valueInputField.text = Editable.Factory.getInstance().newEditable(item.value)
            valueInputField.setOnTouchListener { v, event ->
                item.textWatcher.currencyCode = item.currencyCode
                item.onEditTextFocused()
                false
            }
        } else {
            valueInputField.addTextChangedListener(item.textWatcher)
        }
    }



}