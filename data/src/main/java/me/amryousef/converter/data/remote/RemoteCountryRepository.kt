package me.amryousef.converter.data.remote

import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class RemoteCountryRepository @Inject constructor(
    private val countryCodeService: CountryCodeService,
    private val schedulerProvider: SchedulerProvider
) : CountryRepository {

    override fun getCountryFlagUrl() =
        countryCodeService.getCountryCodes()
            .map {
                it.entries.map { entry ->
                    entry.value to entry.key
                }.toMap()
            }
            .subscribeOn(schedulerProvider.io())
}