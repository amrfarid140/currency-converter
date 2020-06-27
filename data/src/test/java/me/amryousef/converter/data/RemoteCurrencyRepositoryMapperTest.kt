package me.amryousef.converter.data

import com.google.gson.Gson
import me.amryousef.converter.data.remote.RemoteCurrencyRepositoryMapper
import me.amryousef.converter.domain.CurrencyMetadata
import me.amryousef.converter.domain.CurrencyRate
import org.junit.Test
import kotlin.test.assertEquals

class RemoteCurrencyRepositoryMapperTest {

    private val mapper = RemoteCurrencyRepositoryMapper(Gson())

    @Test
    fun givenApiDataHasRatesKey_WhenMap_ThenRatesAreReturned() {
        // Given
        val validApiResponse =
            mapOf(
                "rates" to mapOf("USD" to 22.2),
                "base" to "EUR"
            )
        val validCountries = mapOf(
            "EUR" to "EU",
            "USD" to "USA"
        )
        // When
        val result = mapper.map(validApiResponse, validCountries)

        // Then
        assertEquals(
            expected = result.size,
            actual = 2
        )

        assertEquals(
            expected = CurrencyRate(
                currency = CurrencyMetadata(
                    currencyCode = "EUR",
                    flagUrl = "EU"
                ),
                rate = 1.0,
                isBase = true
            ),
            actual = result.first()
        )
        assertEquals(
            expected = CurrencyRate(
                currency = CurrencyMetadata(
                    currencyCode = "USD",
                    flagUrl = "USA"
                ),
                rate = 22.2
            ),
            actual = result[1]
        )
    }

    @Test(expected = IllegalStateException::class)
    fun givenApiDataNotContainRatesKey_WhenMap_ThenIllegalStateExceptionIsThrown() {
        mapper.map(emptyMap(), emptyMap())
    }
}