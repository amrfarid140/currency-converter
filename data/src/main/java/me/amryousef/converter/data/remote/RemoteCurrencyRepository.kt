package me.amryousef.converter.data.remote


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    private fun x(): Flow<List<CurrencyRate>> = flow<List<CurrencyRate>> {
        repeated()
    }.flowOn(schedulerProvider.io())

    private fun y(): Flow<List<CurrencyRate>> = channelFlow {
        try {
            repeated()
        } catch (e: CancellationException) {
            this.close(cause = e)
        }
    }

    override fun observeCurrencyRates() = y()

    private suspend fun FlowCollector<List<CurrencyRate>>.repeated() {
        val flags = countryRepository.getCountryFlagUrl()
        val latestRates = apiService.getLatestRates()
        emit(mapper.map(latestRates, flags))
        repeated()
    }

    private suspend fun ProducerScope<List<CurrencyRate>>.repeated() {
        try {
            val flags = countryRepository.getCountryFlagUrl()
            val latestRates = apiService.getLatestRates()
            if (!isClosedForSend) {
                offer(mapper.map(latestRates, flags))
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
        } finally {
            delay(TimeUnit.SECONDS.toMillis(5))
            repeated()
        }
    }
}