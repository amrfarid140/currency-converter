package me.amryousef.converter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_code as currencyCode
import kotlinx.android.synthetic.main.row_currency_item.view.currency_row_currency_name as currencyName
import me.amryousef.converter.presentation.ViewStateItem
import java.util.*

class CurrencyRowViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.row_currency_item, parent, false)
) {
    fun bind(item: ViewStateItem) = with(itemView) {
        currencyCode.text = item.currencyCode
        currencyName.text = Currency.getInstance(item.currencyCode).displayName
    }
}