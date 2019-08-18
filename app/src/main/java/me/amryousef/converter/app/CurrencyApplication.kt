package me.amryousef.converter.app

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import me.amryousef.converter.di.DaggerApplicationComponent

open class CurrencyApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().application(this).build()
    }
}