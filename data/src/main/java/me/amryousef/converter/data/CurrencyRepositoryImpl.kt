package me.amryousef.converter.data

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import javax.inject.Inject
import javax.inject.Named

class CurrencyRepositoryImpl @Inject constructor(
    @Named("local") private val localRepository: WritableCurrencyRepository,
    @Named("remote") private val remoteRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository {

    override fun observeCurrencyRates() =
        localRepository.observeCurrencyRates()
            .onStart {
                //TODO: Fix Repeate
                remoteRepository.observeCurrencyRates()
                    .collect {
                        localRepository.addCurrencyRates(it)
                    }
            }.flowOn(schedulerProvider.main())
}