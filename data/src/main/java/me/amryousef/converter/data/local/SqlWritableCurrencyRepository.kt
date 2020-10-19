package me.amryousef.converter.data.local

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.amryousef.converter.data.Database
import me.amryousef.converter.domain.CurrencyMetadata
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import javax.inject.Inject

class SqlWritableCurrencyRepository @Inject constructor(
    private val database: Database,
    private val schedulerProvider: SchedulerProvider
) : WritableCurrencyRepository {

    override suspend fun addCurrencyRates(rates: List<CurrencyRate>) =
        withContext(schedulerProvider.io()) {
            database.transaction {
                rates.forEach {
                    database.currencyQueries.insert(
                        code = it.currency.currencyCode,
                        flag_url = it.currency.flagUrl,
                        is_base = it.isBase
                    )
                    database.currency_RateQueries.insertCurrencyRate(
                        currency_code = it.currency.currencyCode,
                        rate = it.rate.toFloat()
                )
            }
        }
        }

    override fun observeCurrencyRates(): Flow<List<CurrencyRate>> {
        return database.currencyQueries.selectAllWithLatestRate()
            .asFlow()
            .map {
                it.executeAsList().map { row ->
                    CurrencyRate(
                        currency = CurrencyMetadata(
                            currencyCode = row.currency_code,
                            flagUrl = row.flag_url
                        ),
                        isBase = row.is_base,
                        rate = row.rate.toDouble()
                    )
                }
            }.flowOn(schedulerProvider.io())
    }
}