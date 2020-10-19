package me.amryousef.converter.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CurrencyRatesViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestCoroutineDispatcherRule()

    private val mockFetchDataUseCase = mock<FetchDataUseCase>()
    private lateinit var useCaseResultChannel: Channel<UseCaseResult<List<CurrencyData>>>
    private lateinit var subject: CurrencyRatesViewModel

    @Before
    fun setup() {
        useCaseResultChannel = Channel()
        whenever(mockFetchDataUseCase.execute())
            .thenReturn(useCaseResultChannel.receiveAsFlow())
        subject = CurrencyRatesViewModel(mockFetchDataUseCase)
    }

    @Test
    fun givenUseCaseFails_WhenViewModelLoads_ThenStateIsError() {
        useCaseResultChannel.offer(UseCaseResult.Error(Throwable()))
        assertTrue(subject.state.value is ViewState.Error)
    }

    @Test
    fun givenUseCaseSuccess_WhenViewModelLoads_ThenStateIsSuccess() {
        useCaseResultChannel.offer(
            UseCaseResult.Success(
                listOf(
                    CurrencyData(
                        currency = Currency.getInstance("EUR"),
                        rate = 22.2,
                        isBase = false,
                        countryFlagUrl = ""
                    )
                )
            )
        )
        assertTrue(subject.state.value is ViewState.Ready)
    }

    @Test
    fun givenUseCaseSuccessWithEmptyData_WhenViewModelLoads_ThenStateIsLoading() {
        useCaseResultChannel.offer(UseCaseResult.Success(emptyList()))
        assertTrue(subject.state.value is ViewState.Loading)
    }

    @Test
    fun givenUseCaseErrors_WhenRetryClicked_ThenUseCaseReLoaded() {
        useCaseResultChannel.offer(UseCaseResult.Error(Throwable()))
        subject.startFetchingData()
        verify(mockFetchDataUseCase, times(2)).execute()
    }

    @Test
    fun givenStateHasItems_WhenOnRowFocused_ThenFocusedRowIsFirstItem() {
        givenUseCaseReturnsData()

        subject.onRowFocused("USD")

        val state = subject.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = 0,
            actual = state.items.indexOfFirst { it.currencyCode == "USD" }
        )
    }

    @Test
    fun givenBaseRowValueChanged_WhenOnRowValueChanged_ThenAllValuesChangedCorrectly() {
        givenUseCaseReturnsData()

        subject.onRowValueChanged("EUR", "5.0")

        val state = subject.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = "5.0",
            actual = state.items.find { it.currencyCode == "EUR" }?.value
        )
        assertEquals(
            expected = String.format("%.2f", 5.0 * 22.2),
            actual = state.items.find { it.currencyCode == "USD" }?.value
        )
    }

    @Test
    fun givenFocusedRowChanged_WhenUseCaseDataIsUpdated_ThenOrderIsKeptTheSame() {
        givenUseCaseReturnsData()

        subject.onRowFocused("USD")

        givenUseCaseReturnsData()

        val state = subject.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = "USD",
            actual = state.items.first().currencyCode
        )
    }

    @Test
    fun givenOtherRowValueChanged_WhenOnRowValueChanged_ThenAllValuesChangedCorrectly() {
        givenUseCaseReturnsData()

        subject.onRowValueChanged("USD", "5.0")

        val state = subject.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = "5.0",
            actual = state.items.find { it.currencyCode == "USD" }?.value
        )
        assertEquals(
            expected = String.format("%.2f", 5.0 / 22.2),
            actual = state.items.find { it.currencyCode == "EUR" }?.value
        )
    }

    private fun givenUseCaseReturnsData() {
        useCaseResultChannel.offer(
            UseCaseResult.Success(
                listOf(
                    CurrencyData(
                        null,
                        Currency.getInstance("EUR"),
                        1.0,
                        true
                    ),
                    CurrencyData(
                        null,
                        Currency.getInstance("USD"),
                        22.2
                    )
                )
            )
        )
    }
}