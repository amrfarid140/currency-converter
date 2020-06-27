package me.amryousef.converter.domain

import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun observeCurrencyRates(): Flow<List<CurrencyRate>>
}