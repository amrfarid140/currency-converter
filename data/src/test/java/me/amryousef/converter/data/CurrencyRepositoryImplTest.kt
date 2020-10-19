package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import org.junit.Test

@Suppress("EXPERIMENTAL_API_USAGE")
class CurrencyRepositoryImplTest {
    private val mockLocalRepository =
        mock<WritableCurrencyRepository>()
    private val mockRemoteRepository =
        mock<CurrencyRepository>()
    private val testScheduler = TestCoroutineDispatcher()
    private val mockScheduler = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }

    private val repositoryImpl = CurrencyRepositoryImpl(
        localRepository = mockLocalRepository,
        remoteRepository = mockRemoteRepository,
        schedulerProvider = mockScheduler
    )

    @Test
    fun givenRemoteRepositoryHasData_WhenObserveCurrencyRates_ThenLocalRepositoryIsUpdated() =
        runBlockingTest {
            // Given
            val mockCurrencyData = listOf(mock<CurrencyRate>())
            given(mockRemoteRepository.observeCurrencyRates())
                .willReturn(flowOf(mockCurrencyData))
            given(mockLocalRepository.observeCurrencyRates())
                .willReturn(flowOf(emptyList()))

            // When
            repositoryImpl.observeCurrencyRates().collect()

            // Then
            verify(mockLocalRepository).addCurrencyRates(mockCurrencyData)
        }

    @Test
    fun givenRemoteRepositoryErrors_WhenObserveCurrencyRates_ThenLocalDataIsReturned() =
        runBlockingTest {
            // Given
            given(mockRemoteRepository.observeCurrencyRates())
                .willReturn(flow { JsonSyntaxException("Error") })
            given(mockLocalRepository.observeCurrencyRates())
                .willReturn(flowOf(emptyList()))

            // When
            repositoryImpl.observeCurrencyRates().collect()

            // Then
            verify(mockLocalRepository).observeCurrencyRates()
        }

    @Test
    fun givenCurrencyRatesObserved_WhenObserverSubscribed_ThenRemoteDataObserved() =
        runBlockingTest {
            // Given
            given(mockRemoteRepository.observeCurrencyRates())
                .willReturn(flowOf(emptyList()))
            given(mockLocalRepository.observeCurrencyRates())
                .willReturn(flowOf(emptyList()))

            // When
            repositoryImpl.observeCurrencyRates().collect()

            //Then
            verify(mockRemoteRepository).observeCurrencyRates()
        }

    @Test
    fun givenRemoteRepositoryObserved_WhenDataReceived_ThenDataStoredLocally() = runBlockingTest {
        // Given
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(flowOf(listOf(mock())))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(flowOf(emptyList()))
        repositoryImpl.observeCurrencyRates().collect()

        // When
        testScheduler.advanceUntilIdle()

        //Then
        verify(mockLocalRepository).addCurrencyRates(any())
    }
}