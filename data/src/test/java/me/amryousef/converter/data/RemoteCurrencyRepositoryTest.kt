package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import me.amryousef.converter.data.remote.CurrencyRatesService
import me.amryousef.converter.data.remote.RemoteCurrencyRepository
import me.amryousef.converter.data.remote.RemoteCurrencyRepositoryMapper
import org.junit.Test

class RemoteCurrencyRepositoryTest {
    private val mockMapper = mock<RemoteCurrencyRepositoryMapper>()
    private val mockApiService = mock<CurrencyRatesService>()
    private val remoteRepository = RemoteCurrencyRepository(
        apiService = mockApiService,
        mapper = mockMapper
    )

    @Test
    fun givenApiServiceReturnsData_WhenObserveCurrencyRates_ThenApiDataIsMapped() {
        // Given
        given(mockApiService.getLatestRates())
            .willReturn(Single.just(emptyMap()))

        // When
        remoteRepository.observeCurrencyRates().test()

        // Then
        verify(mockMapper).map(any())
    }

    @Test
    fun givenMapperThrowsError_WhenObserveCurrencyRates_ThenOnErrorIsInvoked() {
        // Given
        given(mockMapper.map(any()))
            .willThrow(JsonSyntaxException("Error"))
        given(mockApiService.getLatestRates())
            .willReturn(Single.just(emptyMap()))

        // When
        val observer = remoteRepository.observeCurrencyRates()
            .test()

        // Then
        observer.assertFailure(JsonSyntaxException::class.java)
    }
}