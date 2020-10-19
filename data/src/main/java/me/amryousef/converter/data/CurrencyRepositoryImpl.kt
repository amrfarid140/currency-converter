@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.amryousef.converter.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class CurrencyRepositoryImpl @Inject constructor(
    @Named("local") private val localRepository: WritableCurrencyRepository,
    @Named("remote") private val remoteRepository: CurrencyRepository,
    private val schedulerProvider: SchedulerProvider
) : CurrencyRepository, CoroutineScope {

    private var job: Job? = null
    override val coroutineContext: CoroutineContext
        get() = schedulerProvider.io()

    private fun startRemote() {
        job = launch {
            remoteRepository.observeCurrencyRates()
                .collect {
                    localRepository.addCurrencyRates(it)
                }
        }
    }

    override fun observeCurrencyRates() =
        localRepository.observeCurrencyRates()
            .onStart {
                startRemote()
                emit(emptyList())
            }.onCompletion {
                job?.cancel()
            }
            .flowOn(schedulerProvider.main())
}