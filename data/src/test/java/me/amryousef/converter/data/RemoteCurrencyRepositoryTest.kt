package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.amryousef.converter.data.remote.CurrencyRatesService
import me.amryousef.converter.data.remote.RemoteCurrencyRepository
import me.amryousef.converter.data.remote.RemoteCurrencyRepositoryMapper
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.SchedulerProvider
import org.junit.Test

@Suppress("EXPERIMENTAL_API_USAGE")
class RemoteCurrencyRepositoryTest {
    private val mockMapper = mock<RemoteCurrencyRepositoryMapper>()
    private val mockApiService = mock<CurrencyRatesService>()
    private val mockCountryRepository = mock<CountryRepository>()
    private val testScheduler = TestCoroutineDispatcher()
    private val mockScheduler = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }
    private val remoteRepository = RemoteCurrencyRepository(
        apiService = mockApiService,
        mapper = mockMapper,
        schedulerProvider = mockScheduler,
        countryRepository = mockCountryRepository
    )

    @Test(expected = CancellationException::class)
    fun givenServices_WhenObserveCurrencyRates_ThenBothServicesConsumed() = runBlockingTest {
        // Given
        var thrown = false
        given(mockApiService.getLatestRates())
            .will {
                if (thrown) {
                    this.cancel()
                } else {
                    thrown = true
                    return@will emptyMap<String, Any>()
                }
            }
        given(mockCountryRepository.getCountryFlagUrl())
            .willReturn(emptyMap())

        // When
        remoteRepository
            .observeCurrencyRates()
            .onCompletion {
                verify(mockApiService, atLeastOnce()).getLatestRates()
                verify(mockCountryRepository, atLeastOnce()).getCountryFlagUrl()
            }
            .collect()
    }

    @Test(expected = CancellationException::class)
    fun givenApiServiceReturnsData_WhenObserveCurrencyRates_ThenApiDataIsMapped() =
        runBlockingTest {
            // Given
            var thrown = false
            given(mockApiService.getLatestRates())
                .will {
                    if (thrown) {
                        this.cancel()
                    } else {
                        thrown = true
                        return@will emptyMap<String, Any>()
                    }
                }
            given(mockCountryRepository.getCountryFlagUrl())
                .willReturn(emptyMap())

            // When
            remoteRepository
                .observeCurrencyRates()
                .onCompletion {
                    // Then
                    verify(mockMapper).map(any(), any())
                }
                .collect()
        }

    @Test(expected = CancellationException::class)
    fun givenMapperThrowsError_WhenObserveCurrencyRates_ThenRequestIsRetried() {
        runBlockingTest {
            // Given
            var thrown = false
            given(mockMapper.map(any(), any()))
                .will {
                    if (thrown) {
                        this.cancel()
                    } else {
                        thrown = true
                        throw JsonSyntaxException("Error")
                    }
                }
            given(mockApiService.getLatestRates())
                .willReturn(emptyMap())
            given(mockCountryRepository.getCountryFlagUrl())
                .willReturn(emptyMap())

            // When
            remoteRepository
                .observeCurrencyRates()
                .onCompletion {
                    verify(mockApiService, times(2)).getLatestRates()
                }
                .collect()

        }
    }
}