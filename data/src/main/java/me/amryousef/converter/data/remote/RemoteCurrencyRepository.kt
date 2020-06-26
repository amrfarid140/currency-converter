package me.amryousef.converter.data.remote

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper,
    private val countryRepository: CountryRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {
    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> =
        Single.zip<Map<String, Any>, Map<String, String>, List<CurrencyRate>>(
            apiService.getLatestRates(),
            countryRepository.getCountryFlagUrl(),
            BiFunction { t1, t2 -> mapper.map(t1, t2) }
        ).toObservable().subscribeOn(schedulerProvider.io())
}