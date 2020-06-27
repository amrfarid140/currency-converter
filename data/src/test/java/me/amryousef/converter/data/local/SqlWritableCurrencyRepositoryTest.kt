package me.amryousef.converter.data.local

import com.nhaarman.mockitokotlin2.*
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.reactivex.schedulers.Schedulers
import me.amryousef.converter.data.Database
import me.amryousef.converter.domain.CurrencyMetadata
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.SchedulerProvider
import org.junit.Before
import org.junit.Test

class SqlWritableCurrencyRepositoryTest {

    private val testScheduler = Schedulers.trampoline()
    private val mockSchedulerProvider = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }
    private val spyDatabase = spy(Database(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)))
    private val spyCurrencyQueries = spy(spyDatabase.currencyQueries)
    private val spyRatesQueries = spy(spyDatabase.currency_RateQueries)

    private val subject = SqlWritableCurrencyRepository(
        database = spyDatabase,
        schedulerProvider = mockSchedulerProvider
    )

    @Before
    fun setup() {
        whenever(spyDatabase.currencyQueries).thenReturn(spyCurrencyQueries)
        whenever(spyDatabase.currency_RateQueries).thenReturn(spyRatesQueries)
    }

    @Test
    fun givenCurrencyRates_WhenAddCurrencyRatesCalled_ThenCurrenciesAreAdded() {
        // Given
        val rates = listOf(
            CurrencyRate(
                currency = CurrencyMetadata(
                    currencyCode = "EUR",
                    flagUrl = null
                ),
                isBase = false,
                rate = 22.2
            )
        )

        // When
        subject.addCurrencyRates(rates).test()

        // Then
        verify(spyDatabase).transaction(any(), any())
        verify(spyCurrencyQueries).insert(
            code = "EUR",
            is_base = false,
            flag_url = null
        )
    }
}