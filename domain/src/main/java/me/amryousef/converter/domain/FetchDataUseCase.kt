package me.amryousef.converter.domain

import io.reactivex.Observable
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
                .flatMapToCurrencyData()
                .repeatWhen { complete -> complete.delay(5, TimeUnit.SECONDS) }
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

    private fun Observable<List<CurrencyRate>>.flatMapToCurrencyData() = flatMap { currencies ->
        countryRepository.getCountryFlagUrl()
            .toObservable()
            .map { countries ->
                currencies.toCurrencyData(countries)
            }.subscribeOn(schedulerProvider.io())
    }

    private fun List<CurrencyRate>.toCurrencyData(countriesMap: Map<String, String>) =
        map { currencyRate ->
            val currencyCode = currencyRate.currency.currencyCode
            CurrencyData(
                countryFlagUrl = countriesMap.getCountryFlagUrl(currencyCode),
                currency = currencyRate.currency,
                isBase = currencyRate.isBase,
                rate = currencyRate.rate
            )
        }

    private fun Map<String, String>.getCountryFlagUrl(currencyCode: String) =
        if (currencyCode.toLowerCase() == "eur") {
            FLAG_API.format("eu")
        } else {
            this[currencyCode]?.let { countryCode ->
                FLAG_API.format(countryCode)
            }
        }

    override fun cancel() =
        disposable.clear()
}