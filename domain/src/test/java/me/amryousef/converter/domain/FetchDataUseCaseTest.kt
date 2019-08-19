package me.amryousef.converter.domain

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

private typealias Callback<T> = (UseCaseResult<T>) -> Unit

class FetchDataUseCaseTest {
    private val testScheduler = TestScheduler()
    private val mockCurrencyRepository = mock<WritableCurrencyRepository>()
    private val mockSchedulerProvider = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn Schedulers.trampoline()
    }

    private val useCase = FetchDataUseCase(mockCurrencyRepository, mockSchedulerProvider)

    private val callbackResultCaptor = argumentCaptor<UseCaseResult<List<CurrencyRate>>>()
    private val mockCallback = mock<Callback<List<CurrencyRate>>>()

    @Before
    fun setup() {
        whenever(mockCallback.invoke(callbackResultCaptor.capture())).thenReturn(Unit)
    }

    @Test
    fun givenCurrencyRepositoryHasNewRates_WhenExecute_ThenNewRatesAreStored() {
        val mockData = listOf(mock<CurrencyRate>())
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        verify(mockCurrencyRepository).addCurrencyRates(mockData)
    }

    @Test
    fun givenCurrencyRepositoryOperationsSuccess_WhenExecute_ThenResultIsSuccess() {
        val mockData = listOf(mock<CurrencyRate>())
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )
        given(mockCurrencyRepository.addCurrencyRates(any())).willReturn(Completable.complete())

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Success)
    }

    @Test
    fun givenCurrencyRepositoryFailsToFetchData_WhenExecute_ThenResultIsError() {
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.error(Throwable())
        )

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Error)
    }

    @Test
    fun givenCurrencyRepositoryFailsToAddData_WhenExecute_ThenResultIsError() {
        val mockData = listOf(mock<CurrencyRate>())
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )
        given(mockCurrencyRepository.addCurrencyRates(any())).willReturn(Completable.error(Throwable()))

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Error)
    }

    @Test
    @Ignore
    fun givenObservableCompletes_WhenExecute_ThenOperationsAreRepeatedAfterOneSecond() {
        val mockData = listOf(mock<CurrencyRate>())
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )
        given(mockCurrencyRepository.addCurrencyRates(any())).willReturn(Completable.complete())


        useCase.execute(onResult = mockCallback)
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(mockCurrencyRepository, times(10)).addCurrencyRates(mockData)
        verify(mockCurrencyRepository, times(10)).observeCurrencyRates()

    }

}