package me.amryousef.converter.domain

import io.reactivex.Observable

interface CurrencyRepository {
    fun observeCurrencyRates(): Observable<List<CurrencyRate>>
}