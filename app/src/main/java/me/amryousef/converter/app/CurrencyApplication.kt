package me.amryousef.converter.app

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import me.amryousef.converter.di.DaggerApplicationComponent
import timber.log.Timber

open class CurrencyApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().application(this).build()
    }
}