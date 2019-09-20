package me.amryousef.converter.data.remote

import me.amryousef.converter.domain.CountryRepository
import javax.inject.Inject

class RemoteCountryRepository @Inject constructor(
    private val countryCodeService: CountryCodeService
) : CountryRepository {

    override fun getCountryFlagUrl() =
        countryCodeService.getCountryCodes()
            .map {
                it.entries.map { entry ->
                    entry.value to entry.key
                }.toMap()
            }
}