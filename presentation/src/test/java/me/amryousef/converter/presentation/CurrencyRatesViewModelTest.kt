package me.amryousef.converter.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import me.amryousef.converter.domain.CurrencyData
import me.amryousef.converter.domain.FetchDataUseCase
import me.amryousef.converter.domain.UseCaseResult
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


    @Test
    fun givenUseCaseFails_WhenViewModelLoads_ThenStateIsError() = runBlockingTest {
        whenever(mockFetchDataUseCase.execute()).thenReturn(flowOf(UseCaseResult.Error(Throwable())))
        val viewModel = viewModel()
        assertTrue(viewModel.state.value is ViewState.Error)
    }

    @Test
    fun givenUseCaseSuccess_WhenViewModelLoads_ThenStateIsSuccess() {
        whenever(mockFetchDataUseCase.execute()).thenReturn(
            flowOf(
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
        )
        val viewModel = viewModel()
        assertTrue(viewModel.state.value is ViewState.Ready)
    }

    @Test
    fun givenUseCaseSuccessWithEmptyData_WhenViewModelLoads_ThenStateIsLoading() {
        whenever(mockFetchDataUseCase.execute()).thenReturn(flowOf(UseCaseResult.Success(emptyList())))
        val viewModel = viewModel()
        assertTrue(viewModel.state.value is ViewState.Loading)
    }

//    @Test
//    fun givenUseCaseErrors_WhenRetryClicked_ThenUseCaseReLoaded() {
//        val viewModel = viewModel()
//        callbackCaptor.lastValue.invoke(UseCaseResult.Error(Throwable()))
//
//        viewModel.onRetryClicked()
//
//        verify(mockFetchDataUseCase).cancel()
//        verify(mockFetchDataUseCase, times(2)).execute(eq(null), any())
//    }

    @Test
    fun givenStateHasItems_WhenOnRowFocused_ThenFocusedRowIsFirstItem() {
        givenUseCaseReturnsData()
        val viewModel = viewModel()
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
        givenUseCaseReturnsData()
        val viewModel = viewModel()

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
        givenUseCaseReturnsData()
        val viewModel = viewModel()

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
        givenUseCaseReturnsData()
        val viewModel = viewModel()

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
        whenever(mockFetchDataUseCase.execute())
            .thenReturn(
                flowOf(
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
            )
    }

    private fun viewModel() = CurrencyRatesViewModel(mockFetchDataUseCase).apply {
        state.observeForever { }
    }
}