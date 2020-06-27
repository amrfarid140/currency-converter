package me.amryousef.converter.data.remote


import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteCurrencyRepository @Inject constructor(
    private val apiService: CurrencyRatesService,
    private val mapper: RemoteCurrencyRepositoryMapper,
    private val countryRepository: CountryRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {

    override fun observeCurrencyRates() = flow<List<CurrencyRate>> {
        repeated(countryRepository.getCountryFlagUrl())
    }.flowOn(schedulerProvider.io())

    private suspend fun FlowCollector<List<CurrencyRate>>.repeated(flags: Map<String, String>) {
        val latestRates = apiService.getLatestRates()
        emit(mapper.map(latestRates, flags))
        delay(TimeUnit.SECONDS.toMillis(5))
        repeated(flags)
    }
}