package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.amryousef.converter.data.remote.CurrencyRatesService
import me.amryousef.converter.data.remote.RemoteCurrencyRepository
import me.amryousef.converter.data.remote.RemoteCurrencyRepositoryMapper
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.SchedulerProvider
import org.junit.Test

class RemoteCurrencyRepositoryTest {
    private val mockMapper = mock<RemoteCurrencyRepositoryMapper>()
    private val mockApiService = mock<CurrencyRatesService>()
    private val mockCountryRepository = mock<CountryRepository>()
    private val testScheduler = Schedulers.trampoline()
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

    @Test
    fun givenServices_WhenObserveCurrencyRates_ThenBothServicesConsumed() {
        // Given
        given(mockApiService.getLatestRates())
            .willReturn(Single.just(emptyMap()))
        given(mockCountryRepository.getCountryFlagUrl())
            .willReturn(Single.just(emptyMap()))

        // When
        remoteRepository.observeCurrencyRates().test()

        //Then
        verify(mockApiService).getLatestRates()
        verify(mockCountryRepository).getCountryFlagUrl()
    }

    @Test
    fun givenApiServiceReturnsData_WhenObserveCurrencyRates_ThenApiDataIsMapped() {
        // Given
        given(mockApiService.getLatestRates())
            .willReturn(Single.just(emptyMap()))
        given(mockCountryRepository.getCountryFlagUrl())
            .willReturn(Single.just(emptyMap()))

        // When
        remoteRepository.observeCurrencyRates().test()

        // Then
        verify(mockMapper).map(any(), any())
    }

    @Test
    fun givenMapperThrowsError_WhenObserveCurrencyRates_ThenOnErrorIsInvoked() {
        // Given
        given(mockMapper.map(any(), any()))
            .willThrow(JsonSyntaxException("Error"))
        given(mockApiService.getLatestRates())
            .willReturn(Single.just(emptyMap()))
        given(mockCountryRepository.getCountryFlagUrl())
            .willReturn(Single.just(emptyMap()))

        // When
        val observer = remoteRepository.observeCurrencyRates()
            .test()

        // Then
        observer.assertFailure(JsonSyntaxException::class.java)
    }
}