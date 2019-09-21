package me.amryousef.converter.ui

data class CurrencyRowViewData(
    val countryFlagUrl: String?,
    val currencyCode: String,
    val value: String,
    val isFocused: Boolean,
    val textWatcher: ValueTextWatcher,
    val onEditTextFocused: () -> Unit
)