package me.amryousef.converter.data

import com.google.gson.Gson
import me.amryousef.converter.data.remote.RemoteCurrencyRepositoryMapper
import me.amryousef.converter.domain.CurrencyRate
import org.junit.Test
import java.lang.IllegalStateException
import java.util.Currency
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
        // When
        val result = mapper.map(validApiResponse)

        // Then
        assertEquals(
            expected = result.size,
            actual = 2
        )

        assertEquals(
            expected = CurrencyRate(
                currency = Currency.getInstance("EUR"),
                rate = 1.0,
                isBase = true
            ),
            actual = result.first()
        )
        assertEquals(
            expected = CurrencyRate(
                currency = Currency.getInstance("USD"),
                rate = 22.2
            ),
            actual = result[1]
        )
    }

    @Test(expected = IllegalStateException::class)
    fun givenApiDataNotContainRatesKey_WhenMap_ThenIllegalStateExceptionIsThrown() {
        // Given
        val invalidData = mapOf<String,String>()

        // When
        mapper.map(invalidData)
    }
}