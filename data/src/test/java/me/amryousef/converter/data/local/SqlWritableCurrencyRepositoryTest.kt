package me.amryousef.converter.data.local

import com.nhaarman.mockitokotlin2.*
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.amryousef.converter.data.Database
import me.amryousef.converter.domain.CurrencyMetadata
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.SchedulerProvider
import org.junit.Before
import org.junit.Test

@Suppress("EXPERIMENTAL_API_USAGE")
class SqlWritableCurrencyRepositoryTest {

    private val testScheduler = TestCoroutineDispatcher()
    private val mockSchedulerProvider = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }
    private val spyDatabase = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).run {
        Database.Schema.create(this)
        spy(Database(this))
    }
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
    fun givenCurrencyRates_WhenAddCurrencyRatesCalled_ThenCurrenciesAreAdded() = runBlockingTest {
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
        subject.addCurrencyRates(rates)

        // Then
        verify(spyDatabase).transaction(any(), any())
        verify(spyCurrencyQueries).insert(
            code = "EUR",
            is_base = false,
            flag_url = null
        )
    }
}