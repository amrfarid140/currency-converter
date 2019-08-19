package me.amryousef.converter.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.amryousef.converter.ui.CurrencyActivity

@Module
abstract class UiModule {
    @ContributesAndroidInjector
    abstract fun contributeActivityInjector(): CurrencyActivity
}