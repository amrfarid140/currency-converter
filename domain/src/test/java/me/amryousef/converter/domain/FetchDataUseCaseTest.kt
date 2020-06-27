package me.amryousef.converter.domain

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

private typealias Callback<T> = (UseCaseResult<T>) -> Unit

class FetchDataUseCaseTest {

    private val testScheduler = Schedulers.trampoline()
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
    fun givenCurrencyRepositoryOperationsSuccess_WhenExecute_ThenResultIsSuccess() {
        val mockData = givenMockCurrencyRates()
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )

        useCase.execute(onResult = mockCallback)

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Success)
    }

    @Test
    fun givenCurrencyRepositoryFailsToFetchData_WhenExecute_ThenResultIsError() {
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.error(Throwable())
        )

        useCase.execute(onResult = mockCallback)

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Error)
    }

    private fun givenMockCurrencyRates() = listOf(
        CurrencyRate(
            currency = CurrencyMetadata("EUR","test"),
            isBase = true,
            rate = 22.2
        )
    )
}