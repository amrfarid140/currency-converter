package me.amryousef.converter.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.amryousef.converter.presentation.CurrencyRatesViewModel
import me.amryousef.converter.ui.ViewModelFactory
import me.amryousef.converter.ui.ViewModelKey
import javax.inject.Singleton

@Module
abstract class PresentationModule {
    @Binds
    @Singleton
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CurrencyRatesViewModel::class)
    abstract fun bindViewModel(viewModel: CurrencyRatesViewModel): ViewModel
}