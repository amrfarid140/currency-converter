package me.amryousef.converter.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import me.amryousef.converter.app.CurrencyApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DomainModule::class,
        DataModule::class,
        AndroidInjectionModule::class,
        PresentationModule::class,
        UiModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DaggerApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: CurrencyApplication): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: CurrencyApplication)
}