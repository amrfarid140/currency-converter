package me.amryousef.converter.di

import dagger.Binds
import dagger.Module
import me.amryousef.converter.app.AndroidSchedulerProvider
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Singleton

@Module
abstract class DomainModule {
    @Singleton
    @Binds
    abstract fun bindSchedulerProvider(androidSchedulerProvider: AndroidSchedulerProvider): SchedulerProvider
}