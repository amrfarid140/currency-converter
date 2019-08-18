package me.amryousef.converter.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import me.amryousef.converter.data.local.LocalWritableCurrencyRepository
import me.amryousef.converter.domain.CurrencyRate
import org.junit.Test
import java.util.Currency

class LocalWritableCurrencyRepositoryTest {
    private val mockSharedPreferencesEditor = mock<SharedPreferences.Editor>()
    private val mockSharedPreferences = mock<SharedPreferences> {
        on { edit() } doReturn mockSharedPreferencesEditor
    }
    private val gson = Gson()
    private val localWritableCurrencyRepository =
        LocalWritableCurrencyRepository(
            mockSharedPreferences,
            gson
        )

    @Test
    fun givenNewRates_WhenAddCurrencyRate_ThenRatesStoredInSharedPreferences() {
        // Given
        val newData = listOf(
            CurrencyRate(
                currency = Currency.getInstance("USD"),
                rate = 22.2
            )
        )

        // When
        localWritableCurrencyRepository
            .addCurrencyRates(newData)
            .test()

        // Then
        verify(mockSharedPreferencesEditor).putString(
            eq("data"),
            eq("[{\"currencyCode\":\"USD\",\"rate\":22.2}]")
        )
    }

    @Test
    fun givenSharedPreferencesHasRates_WhenObserveCurrencyRates_ThenCurrencyRateAreReturned() {
        // Given
        given(mockSharedPreferences.getString(eq("data"), anyOrNull()))
            .willReturn("[{\"currencyCode\":\"USD\",\"rate\":22.2}]")

        // When
        val observer = localWritableCurrencyRepository.observeCurrencyRates().test()

        // Then
        observer.assertValue { result ->
            result.size == 1 &&
                result.first() == CurrencyRate(
                Currency.getInstance("USD"), 22.2
            )
        }
    }
}