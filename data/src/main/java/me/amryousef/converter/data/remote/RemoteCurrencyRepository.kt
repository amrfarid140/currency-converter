package me.amryousef.converter.data.remote

import me.amryousef.converter.data.CurrencyRatesService
import me.amryousef.converter.domain.CurrencyRepository
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper
) : CurrencyRepository {
    override fun observeCurrencyRates() =
        apiService
            .getLatestRates()
            .map {
                apiData ->
                mapper.map(apiData)
            }
            .toObservable()
}