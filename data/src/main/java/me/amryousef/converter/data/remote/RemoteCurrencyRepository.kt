package me.amryousef.converter.data.remote

import io.reactivex.Observable
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper
) : CurrencyRepository {
    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> =
        apiService
            .getLatestRates()
            .map { apiData -> mapper.map(apiData) }
            .toObservable()
}