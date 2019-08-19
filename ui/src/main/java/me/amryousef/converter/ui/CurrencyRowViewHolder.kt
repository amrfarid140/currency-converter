package me.amryousef.converter.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_code as currencyCode
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_name as currencyName
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_value_input_field as valueInputField
import java.util.*

class CurrencyRowViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.row_currency_item, parent, false)
) {
    fun bind(item: CurrencyRowViewData) = with(itemView) {
        currencyCode.text = item.currencyCode
        val currency = Currency.getInstance(item.currencyCode)
        currencyName.text = currency.displayName
        valueInputField.text = Editable.Factory.getInstance().newEditable(item.value)
    }


}