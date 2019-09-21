package me.amryousef.converter.ui

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CurrencyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, "me.amryousef.converter.ui.TestApplication", context)
    }
}