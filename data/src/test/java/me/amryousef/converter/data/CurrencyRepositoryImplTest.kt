package me.amryousef.converter.data

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import me.amryousef.converter.domain.CurrencyRate
import me.amryousef.converter.domain.CurrencyRepository
import me.amryousef.converter.domain.SchedulerProvider
import me.amryousef.converter.domain.WritableCurrencyRepository
import org.junit.Test

class CurrencyRepositoryImplTest {
    private val mockLocalRepository =
        mock<WritableCurrencyRepository>()
    private val mockRemoteRepository =
        mock<CurrencyRepository>()
    private val testScheduler = Schedulers.trampoline()
    private val mockScheduler = mock<SchedulerProvider> {
        on { io() } doReturn testScheduler
        on { main() } doReturn testScheduler
    }

    private val repositoryImpl = CurrencyRepositoryImpl(
        localRepository = mockLocalRepository,
        remoteRepository = mockRemoteRepository,
        schedulerProvider = mockScheduler
    )

    @Test
    fun givenRemoteRepositoryHasData_WhenObserveCurrencyRates_ThenLocalRepositoryIsUpdated() {
        // Given
        val mockCurrencyData = listOf(mock<CurrencyRate>())
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(Observable.just(mockCurrencyData))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))

        // When
        repositoryImpl.observeCurrencyRates().test()

        // Then
        verify(mockLocalRepository).addCurrencyRates(mockCurrencyData)
    }

    @Test
    fun givenRemoteRepositoryErrors_WhenObserveCurrencyRates_ThenLocalDataIsReturned() {
        // Given
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(Observable.error(JsonSyntaxException("Test")))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))

        // When
        repositoryImpl.observeCurrencyRates().test()

        // Then
        verify(mockLocalRepository).observeCurrencyRates()
    }

    @Test
    fun givenCurrencyRatesObserved_WhenObserverSubscribed_ThenRemoteDataObserved() {
        // Given
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))
        val observable = repositoryImpl.observeCurrencyRates()

        // When
        observable.test()

        //Then
        verify(mockRemoteRepository).observeCurrencyRates()
    }

    @Test
    fun givenRemoteRepositoryObserved_WhenDataReceived_ThenDataStoredLocally() {
        // Given
        val subject = PublishSubject.create<List<CurrencyRate>>()
        given(mockRemoteRepository.observeCurrencyRates())
            .willReturn(subject)
        given(mockLocalRepository.observeCurrencyRates())
            .willReturn(Observable.just(emptyList()))
        repositoryImpl.observeCurrencyRates().test()

        // When
        subject.onNext(emptyList())

        //Then
        verify(mockLocalRepository).addCurrencyRates(any())
    }
}