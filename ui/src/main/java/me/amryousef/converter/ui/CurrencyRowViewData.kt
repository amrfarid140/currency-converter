package me.amryousef.converter.ui

import android.text.TextWatcher

data class CurrencyRowViewData(
    val currencyCode: String,
    val value: String,
    val textWatcher: TextWatcher,
    val onEditTextFocused: () -> Unit
)