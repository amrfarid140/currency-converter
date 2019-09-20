package me.amryousef.converter.domain

import io.reactivex.Single

interface CountryRepository {
    fun getCountryFlagUrl(): Single<Map<String, String>>
}