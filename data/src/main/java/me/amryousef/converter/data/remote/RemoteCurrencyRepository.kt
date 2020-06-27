package me.amryousef.converter.data.remote


import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper,
    private val countryRepository: CountryRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {

    override fun observeCurrencyRates() = flow {
        val latestRates = apiService.getLatestRates()
        val flags = countryRepository.getCountryFlagUrl()
        emit(mapper.map(latestRates, flags))
    }.flowOn(schedulerProvider.io())
}