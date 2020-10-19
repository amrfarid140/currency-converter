package me.amryousef.converter.domain

interface CountryRepository {
    suspend fun getCountryFlagUrl(): Map<String, String>
}