package me.amryousef.converter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CurrencyListAdapter : ListAdapter<CurrencyRowViewData, CurrencyRowViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrencyRowViewHolder(parent)

    override fun onBindViewHolder(holder: CurrencyRowViewHolder, position: Int) =
        holder.bind(getItem(position))

    private class DiffUtilCallback : DiffUtil.ItemCallback<CurrencyRowViewData>() {
        override fun areItemsTheSame(oldItem: CurrencyRowViewData, newItem: CurrencyRowViewData): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: CurrencyRowViewData, newItem: CurrencyRowViewData): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
                && oldItem.value == newItem.value
                && oldItem.isFocused == newItem.isFocused
        }
    }
}