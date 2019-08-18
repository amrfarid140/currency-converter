package me.amryousef.converter.domain

import io.reactivex.Completable

interface WritableCurrencyRepository : CurrencyRepository {
    fun addCurrencyRates(rates: List<CurrencyRate>): Completable
}