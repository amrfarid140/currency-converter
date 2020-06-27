package me.amryousef.converter.domain

interface WritableCurrencyRepository : CurrencyRepository {
    suspend fun addCurrencyRates(rates: List<CurrencyRate>)
}