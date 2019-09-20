package me.amryousef.converter.domain

import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val countryRepository: CountryRepository,
    private val schedulerProvider: SchedulerProvider
) : UseCase<Nothing, List<CurrencyData>> {

    companion object {
        const val FLAG_API = "https://www.countryflags.io/%s/flat/64.png"
    }

    private val disposable = CompositeDisposable()

    override fun execute(input: Nothing?, onResult: (UseCaseResult<List<CurrencyData>>) -> Unit) {
        disposable.add(
            currencyRepository
                .observeCurrencyRates()
                .flatMap { currencies ->
                    countryRepository.getCountryFlagUrl()
                        .toObservable()
                        .map { countries ->
                            currencies.map { currencyRate ->
                                val currencyCode = currencyRate.currency.currencyCode
                                CurrencyData(
                                    countryFlagUrl = if (currencyCode.toLowerCase() == "eur") {
                                        FLAG_API.format("eu")
                                    } else {
                                        countries[currencyRate.currency.currencyCode]?.let { countryCode ->
                                            FLAG_API.format(countryCode)
                                        }
                                    },
                                    currency = currencyRate.currency,
                                    isBase = currencyRate.isBase,
                                    rate = currencyRate.rate
                                )
                            }
                        }
                }
                .repeatWhen { complete -> complete.delay(1, TimeUnit.SECONDS) }
                .observeOn(schedulerProvider.main())
                .subscribeOn(schedulerProvider.io())
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