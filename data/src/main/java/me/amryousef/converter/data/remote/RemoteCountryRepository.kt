package me.amryousef.converter.data.remote

import kotlinx.coroutines.withContext
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class RemoteCountryRepository @Inject constructor(
    private val countryCodeService: CountryCodeService,
    private val schedulerProvider: SchedulerProvider
) : CountryRepository {

    companion object {
        const val FLAG_API = "https://www.countryflags.io/%s/flat/64.png"
    }

    override suspend fun getCountryFlagUrl() = withContext(schedulerProvider.io()) {
        val codes = countryCodeService.getCountryCodes()
        codes.map { entry ->
            entry.value to FLAG_API.format(entry.key)
        }.toMap()
    }
}