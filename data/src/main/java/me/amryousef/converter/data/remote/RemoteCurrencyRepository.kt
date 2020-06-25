package me.amryousef.converter.data.remote

import android.util.Log
import io.reactivex.Observable
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {
    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> =
        apiService
            .getLatestRates()
            .map { apiData ->
                mapper.map(apiData)
            }
            .toObservable()
            .subscribeOn(schedulerProvider.io())
}