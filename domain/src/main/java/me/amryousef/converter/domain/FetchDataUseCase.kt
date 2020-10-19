package me.amryousef.converter.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : NoArgUseCase<List<CurrencyData>> {

    override fun execute(): Flow<UseCaseResult<List<CurrencyData>>> =
        currencyRepository
            .observeCurrencyRates()
            .map { rates ->
                UseCaseResult.Success(
                    rates.map { rate ->
                        CurrencyData(
                            currency = Currency.getInstance(rate.currency.currencyCode),
                            rate = rate.rate,
                            isBase = rate.isBase,
                            countryFlagUrl = rate.currency.flagUrl
                        )
                    }
                ) as UseCaseResult<List<CurrencyData>>
            }
            .catch {
                emit(UseCaseResult.Error(it))
            }.flowOn(schedulerProvider.io())
}