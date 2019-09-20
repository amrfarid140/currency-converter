package me.amryousef.converter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CurrencyListAdapter : RecyclerView.Adapter<CurrencyRowViewHolder>() {

    private val data = mutableListOf<CurrencyRowViewData>()

    init {
        setHasStableIds(true)
    }

    fun submitList(viewData: List<CurrencyRowViewData>) {
        data.clear()
        data.addAll(viewData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrencyRowViewHolder(parent)

    override fun onBindViewHolder(holder: CurrencyRowViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemId(position: Int): Long {
        return getItem(position).currencyCode.hashCode().toLong()
    }

    private fun getItem(position: Int) = data[position]

    override fun getItemCount() = data.size
}
