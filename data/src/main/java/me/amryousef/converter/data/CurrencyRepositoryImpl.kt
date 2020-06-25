package me.amryousef.converter.data

import android.util.Log
import io.reactivex.Observable
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import javax.inject.Inject
import javax.inject.Named

class CurrencyRepositoryImpl @Inject constructor(
    @Named("local") private val localRepository: WritableCurrencyRepository,
    @Named("remote") private val remoteRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {

    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> =
        remoteRepository
            .observeCurrencyRates()
            .flatMap { data ->
                localRepository.addCurrencyRates(data).andThen(Observable.just(data))
            }
            .onErrorResumeNext(localRepository.observeCurrencyRates())
            .subscribeOn(schedulerProvider.io())
}