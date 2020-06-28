package me.amryousef.converter.data.remote


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
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

    override fun observeCurrencyRates(): Flow<List<CurrencyRate>> = channelFlow {
        try {
            repeated()
        } catch (e: CancellationException) {
            this.close(cause = e)
        }
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