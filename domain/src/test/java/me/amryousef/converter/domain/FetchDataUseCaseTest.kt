package me.amryousef.converter.domain

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

private typealias Callback<T> = (UseCaseResult<T>) -> Unit

class FetchDataUseCaseTest {

    private val testScheduler = TestCoroutineDispatcher()
    private val mockCurrencyRepository = mock<CurrencyRepository>()
    private val mockSchedulerProvider = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }

    private val useCase = FetchDataUseCase(
        mockCurrencyRepository,
        mockSchedulerProvider
    )

    private val callbackResultCaptor = argumentCaptor<UseCaseResult<List<CurrencyData>>>()
    private val mockCallback = mock<Callback<List<CurrencyData>>>()

    @Before
    fun setup() {
        whenever(mockCallback.invoke(callbackResultCaptor.capture())).thenReturn(Unit)
    }

    @Test
    fun givenCurrencyRepositoryOperationsSuccess_WhenExecute_ThenResultIsSuccess() =
        runBlockingTest {
            val mockData = givenMockCurrencyRates()
            given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
                flowOf(mockData)
            )

            assertTrue(useCase.execute().toList().first() is UseCaseResult.Success)
        }

    @Test
    fun givenCurrencyRepositoryFailsToFetchData_WhenExecute_ThenResultIsError() = runBlockingTest {
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            flow { throw Throwable() }
        )

        assertTrue(useCase.execute().toList().first() is UseCaseResult.Error)
    }

    private fun givenMockCurrencyRates() = listOf(
        CurrencyRate(
            currency = CurrencyMetadata("EUR", "test"),
            isBase = true,
            rate = 22.2
        )
    )
}