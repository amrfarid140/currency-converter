package me.amryousef.converter.domain

import java.util.Currency

data class CurrencyData(
    val countryFlagUrl: String?,
    val currency: Currency,
    val rate: Double,
    val isBase: Boolean = false
)