package me.amryousef.converter.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import me.amryousef.converter.app.CurrencyApplication
import me.amryousef.converter.data.CurrencyRepositoryImpl
import me.amryousef.converter.data.remote.CurrencyRatesService
import me.amryousef.converter.data.local.LocalWritableCurrencyRepository
import me.amryousef.converter.data.remote.CountryCodeService
import me.amryousef.converter.data.remote.RemoteCountryRepository
import me.amryousef.converter.data.remote.RemoteCurrencyRepository
import me.amryousef.converter.domain.CountryRepository
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.WritableCurrencyRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: CurrencyApplication) =
        application.getSharedPreferences("local_store", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build())
            .baseUrl("https://us-central1-gentle-studio-241820.cloudfunctions.net/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): CurrencyRatesService =
        retrofit.create(CurrencyRatesService::class.java)

    @Singleton
    @Provides
    fun provideCountryCodeService(retrofit: Retrofit): CountryCodeService =
        retrofit.create(CountryCodeService::class.java)

    @Named("local")
    @Singleton
    @Provides
    fun provideLocalRepository(
        local: LocalWritableCurrencyRepository
    ): WritableCurrencyRepository = local

    @Named("remote")
    @Singleton
    @Provides
    fun provideRemoteRepository(
        remote: RemoteCurrencyRepository
    ): CurrencyRepository = remote

    @Singleton
    @Provides
    fun provideCountryRepository(
        countryRepository: RemoteCountryRepository
    ): CountryRepository = countryRepository

    @Singleton
    @Provides
    fun provideRepositoryImpl(
        repository: CurrencyRepositoryImpl
    ): CurrencyRepository = repository
}