package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.WritableCurrencyRepository
import org.junit.Test

class CurrencyRepositoryImplTest {
    private val mockLocalRepository =
        mock<WritableCurrencyRepository>()
    private val mockRemoteRepository =
        mock<CurrencyRepository>()

    private val repositoryImpl = CurrencyRepositoryImpl(
        localRepository = mockLocalRepository,
        remoteRepository = mockRemoteRepository
    )

    @Test
    fun givenRemoteRepositoryHasData_WhenObserveCurrencyRates_ThenLocalRepositoryIsUpdated() {
        // Given
        val mockCurrencyData = listOf(mock<CurrencyRate>())
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(Observable.just(mockCurrencyData))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))

        // When
        repositoryImpl.observeCurrencyRates().test()

        // Then
        verify(mockLocalRepository).addCurrencyRates(mockCurrencyData)
    }

    @Test
    fun givenRemoteRepositoryErrors_WhenObserveCurrencyRates_ThenLocalDataIsReturned() {
        // Given
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(Observable.error(JsonSyntaxException("Test")))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))

        // When
        repositoryImpl.observeCurrencyRates().test()

        // Then
        verify(mockLocalRepository).observeCurrencyRates()
    }
}