package me.amryousef.converter.presentation

import androidx.lifecycle.LifecycleObserver

interface CurrencyRatesPresenter : LifecycleObserver {
    fun bindView(view: CurrencyRatesView)
    fun onRowFocused(currencyCode: String)
    fun onRowValueChanged(currencyCode: String, newValueText: String)
    fun retryFetchingData()
}