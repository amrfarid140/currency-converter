package me.amryousef.converter.domain

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.Currency
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private typealias Callback<T> = (UseCaseResult<T>) -> Unit

class FetchDataUseCaseTest {
    
    private companion object {
        const val SOME_CURRENCY_CODE = "EGP"
        val SOME_COUNTRY_DATA = mapOf(
            "EGP" to "EGY",
            "EUR" to "ESP",
            "EUR" to "FRA"
        )
    }
    
    private val testScheduler = TestScheduler()
    private val mockCurrencyRepository = mock<CurrencyRepository>()
    private val mockCountryRepository = mock<CountryRepository>()
    private val mockSchedulerProvider = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn Schedulers.trampoline()
    }

    private val useCase = FetchDataUseCase(
        mockCurrencyRepository,
        mockCountryRepository,
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
        given(mockCountryRepository.getCountryFlagUrl()).willReturn(
            Single.just(SOME_COUNTRY_DATA)
        )

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        assertTrue(callbackResultCaptor.lastValue is UseCaseResult.Success)
    }

    @Test
    fun givenCurrencyIsEuro_WhenExecute_ThenResultHasEUFlagUrl() {
        val mockData = givenMockEuroCurrencyRates()
        given(mockCurrencyRepository.observeCurrencyRates()).willReturn(
            Observable.just(mockData)
        )
        given(mockCountryRepository.getCountryFlagUrl()).willReturn(
            Single.just(SOME_COUNTRY_DATA)
        )

        useCase.execute(onResult = mockCallback)
        testScheduler.triggerActions()

        val actualValue = callbackResultCaptor.lastValue
        assertTrue(actualValue is UseCaseResult.Success)
        val actualData = actualValue.data.firstOrNull()
        assertNotNull(actualData)
        assertEquals(
            actual = actualData.countryFlagUrl,
            expected = "https://www.countryflags.io/eu/flat/64.png"
        )

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

    private fun givenMockEuroCurrencyRates() = listOf(
        mock<CurrencyRate> {
            on { currency } doReturn Currency.getInstance("EUR")
        }
    )

    private fun givenMockCurrencyRates() = listOf(
        mock<CurrencyRate> {
            on { currency } doReturn Currency.getInstance(SOME_CURRENCY_CODE)
        }
    )
}