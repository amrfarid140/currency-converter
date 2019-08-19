package me.amryousef.converter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.amryousef.converter.presentation.ViewStateItem

class CurrencyListAdapter : ListAdapter<ViewStateItem, CurrencyRowViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrencyRowViewHolder(parent)


    override fun onBindViewHolder(holder: CurrencyRowViewHolder, position: Int) =
        holder.bind(getItem(position))


    private companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ViewStateItem>() {
            override fun areItemsTheSame(oldItem: ViewStateItem, newItem: ViewStateItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ViewStateItem,
                newItem: ViewStateItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}