package me.amryousef.converter.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Currency
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CurrencyRatesViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val callbackCaptor = argumentCaptor<(UseCaseResult<List<CurrencyData>>) -> Unit>()
    private val mockFetchDataUseCase = mock<FetchDataUseCase>()

    @Before
    fun setup() {
        doNothing().whenever(mockFetchDataUseCase)
            .execute(eq(null), callbackCaptor.capture())
    }

    @Test
    fun givenUseCaseFails_WhenViewModelLoads_ThenStateIsError() {
        val viewModel = viewModel()
        callbackCaptor.lastValue.invoke(UseCaseResult.Error(Throwable()))

        assertTrue(viewModel.state.value is ViewState.Error)
    }

    @Test
    fun givenUseCaseIsLoading_WhenViewModelExecutesUseCase_ThenStateIsLoading() {
        val viewModel = viewModel()
        assertTrue(viewModel.state.value is ViewState.Loading)
    }

    @Test
    fun givenUseCaseSuccess_WhenViewModelLoads_ThenStateIsSuccess() {
        val viewModel = viewModel()
        callbackCaptor.lastValue.invoke(UseCaseResult.Success(emptyList()))
        assertTrue(viewModel.state.value is ViewState.Ready)
    }

    @Test
    fun givenUseCaseErrors_WhenRetryClicked_ThenUseCaseReLoaded() {
        val viewModel = viewModel()
        callbackCaptor.lastValue.invoke(UseCaseResult.Error(Throwable()))

        viewModel.onRetryClicked()

        verify(mockFetchDataUseCase).cancel()
        verify(mockFetchDataUseCase, times(2)).execute(eq(null), any())
    }

    @Test
    fun givenStateHasItems_WhenOnRowFocused_ThenFocusedRowIsFirstItem() {
        val viewModel = viewModel()
        givenUseCaseReturnsData()

        viewModel.onRowFocused("USD")

        val state = viewModel.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = 0,
            actual = state.items.indexOfFirst { it.currencyCode == "USD" }
        )
    }

    @Test
    fun givenBaseRowValueChanged_WhenOnRowValueChanged_ThenAllValuesChangedCorrectly() {
        val viewModel = viewModel()
        givenUseCaseReturnsData()

        viewModel.onRowValueChanged("EUR", "5.0")

        val state = viewModel.state.value
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
        val viewModel = viewModel()
        givenUseCaseReturnsData()

        viewModel.onRowFocused("USD")

        givenUseCaseReturnsData()

        val state = viewModel.state.value
        assertNotNull(state)
        assertTrue(state is ViewState.Ready)
        assertEquals(
            expected = "USD",
            actual = state.items.first().currencyCode
        )
    }

    @Test
    fun givenOtherRowValueChanged_WhenOnRowValueChanged_ThenAllValuesChangedCorrectly() {
        val viewModel = viewModel()
        givenUseCaseReturnsData()

        viewModel.onRowValueChanged("USD", "5.0")

        val state = viewModel.state.value
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
        callbackCaptor.lastValue.invoke(
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

    private fun viewModel() =
        CurrencyRatesViewModel(mockFetchDataUseCase).apply {
            onViewStarted()
        }
}