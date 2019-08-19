package me.amryousef.converter.ui

data class CurrencyRowViewData(
    val currencyCode: String,
    val value: String,
    val isFocused: Boolean,
    val textWatcher: CurrencyActivity.ValueTextWatcher,
    val onEditTextFocused: () -> Unit
)