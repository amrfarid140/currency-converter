package me.amryousef.converter.data.local

import com.squareup.sqldelight.runtime.rx.asObservable
import io.reactivex.Completable
import io.reactivex.Observable
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

    override fun addCurrencyRates(rates: List<CurrencyRate>) = Completable.fromAction {
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
    }.subscribeOn(schedulerProvider.io())

    override fun observeCurrencyRates(): Observable<List<CurrencyRate>> {
        return database.currencyQueries.selectAllWithLatestRate()
            .asObservable(scheduler = schedulerProvider.io())
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
            }
    }
}