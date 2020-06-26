package me.amryousef.converter.data

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class CurrencyRepositoryImpl @Inject constructor(
    @Named("local") private val localRepository: WritableCurrencyRepository,
    @Named("remote") private val remoteRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {

    private var disposable: Disposable? = null


    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> =
        localRepository.observeCurrencyRates()
            .doOnSubscribe {
                if (disposable == null) {
                    disposable = remoteRepository.observeCurrencyRates()
                        .flatMapCompletable { localRepository.addCurrencyRates(it) }
                        .onErrorComplete()
                        .repeatWhen { complete -> complete.delay(5, TimeUnit.SECONDS) }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe()
                }
            }.doOnDispose {
                disposable?.dispose()
                disposable = null
            }
            .subscribeOn(schedulerProvider.io())
}