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
import me.amryousef.converter.data.remote.RemoteCurrencyRepository
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
    fun provideApiService(gson: Gson): CurrencyRatesService =
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build())
            .baseUrl("https://revolut.duckdns.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CurrencyRatesService::class.java)

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
    fun provideRepositoryImpl(
        repository: CurrencyRepositoryImpl
    ): CurrencyRepository = repository
}