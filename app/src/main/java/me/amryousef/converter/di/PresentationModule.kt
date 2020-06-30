package me.amryousef.converter.di

import dagger.Binds
import dagger.Module
import me.amryousef.converter.presentation.CurrencyRatesPresenter
import me.amryousef.converter.presentation.CurrencyRatesPresenterImpl

@Module
abstract class PresentationModule {
    @Binds
    abstract fun bindPresenter(presenterImpl: CurrencyRatesPresenterImpl): CurrencyRatesPresenter
}