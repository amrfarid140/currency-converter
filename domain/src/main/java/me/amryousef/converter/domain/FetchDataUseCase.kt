package me.amryousef.converter.domain

import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : UseCase<Nothing, List<CurrencyRate>> {
    private val disposable = CompositeDisposable()

    override fun execute(input: Nothing?, onResult: (UseCaseResult<List<CurrencyRate>>) -> Unit) {
        disposable.add(
            currencyRepository
                .observeCurrencyRates()
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
        disposable.dispose()
}