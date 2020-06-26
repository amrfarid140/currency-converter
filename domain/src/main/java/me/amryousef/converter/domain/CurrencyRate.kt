package me.amryousef.converter.domain

data class CurrencyRate(
    val currency: CurrencyMetadata,
    val rate: Double,
    val isBase: Boolean = false
)

data class CurrencyMetadata(val currencyCode: String, val flagUrl: String?)