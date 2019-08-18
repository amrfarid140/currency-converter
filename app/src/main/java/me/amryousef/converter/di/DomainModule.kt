package me.amryousef.converter.di

import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import io.reactivex.Scheduler
import me.amryousef.converter.app.AndroidSchedulerProvider
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Singleton

@Module
abstract class DomainModule {
    @Singleton
    @Binds
    abstract fun bindSchedulerProvider(androidSchedulerProvider: AndroidSchedulerProvider): SchedulerProvider

    @Binds
    abstract fun bindFetchDataUseCase(useCase: FetchDataUseCase): FetchDataUseCase
}