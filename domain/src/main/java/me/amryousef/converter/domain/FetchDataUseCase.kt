package me.amryousef.converter.domain

import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : UseCase<Nothing, List<CurrencyData>> {

    private val disposable = CompositeDisposable()

    override fun execute(input: Nothing?, onResult: (UseCaseResult<List<CurrencyData>>) -> Unit) {
        disposable.add(
            currencyRepository
                .observeCurrencyRates()
                .map { rates ->
                    rates.map { rate ->
                        CurrencyData(
                            currency = Currency.getInstance(rate.currency.currencyCode),
                            rate = rate.rate,
                            isBase = rate.isBase,
                            countryFlagUrl = rate.currency.flagUrl
                        )
                    }
                }
                .observeOn(schedulerProvider.main())
                .subscribe(
                    { data ->
                        onResult(UseCaseResult.Success(data))
                    },
                    { error ->
                        onResult(UseCaseResult.Error(error))
                    }
                )
        )
    }

    override fun cancel() =
        disposable.clear()
}