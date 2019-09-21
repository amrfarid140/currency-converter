package me.amryousef.converter.ui

import android.app.Activity
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import io.mockk.every
import io.mockk.mockk
import me.amryousef.converter.presentation.CurrencyRatesViewModel
import me.amryousef.converter.presentation.ViewState
import org.junit.Rule
import org.junit.Test
import javax.inject.Provider

class CurrencyActivityTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val testState = MutableLiveData<ViewState>()
    private val mockViewModel = mockk<CurrencyRatesViewModel>(relaxUnitFun = true)
    private val mockViewModelFactory = mockk<ViewModelFactory>()

    @get:Rule
    val activityRule = object : ActivityTestRule<CurrencyActivity>(CurrencyActivity::class.java) {
        @Suppress("UNCHECKED_CAST")
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            every { mockViewModelFactory.create(CurrencyRatesViewModel::class.java) } returns mockViewModel
            every { mockViewModel.state } returns testState

            (ApplicationProvider.getApplicationContext<Context>() as? TestApplication)?.let {
                it.dispatchingAndroidInjector = createFakeMainActivityInjector {
                    viewModelFactory = mockViewModelFactory
                } as AndroidInjector<Any>
            }
        }
    }

    fun createFakeMainActivityInjector(block: CurrencyActivity.() -> Unit)
            : DispatchingAndroidInjector<Activity> {
        val injector = AndroidInjector<Activity> { instance ->
            if (instance is CurrencyActivity) {
                instance.block()
            }
        }
        val factory = AndroidInjector.Factory<Activity> { injector }
        val map = mapOf(
            Pair<Class<*>,
                    Provider<AndroidInjector.Factory<*>>>(
                CurrencyActivity::class.java,
                Provider { factory })
        )
        return DispatchingAndroidInjector_Factory.newInstance(map, emptyMap())
    }


    @Test
    fun givenStateIsError_WhenActivityLoads_ThenErrorStateIsDisplayed() {

        activityRule.runOnUiThread {
            testState.value = ViewState.Error
        }

        onView(withId(R.id.activity_currency_error_message)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_currency_retry_button)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_currency_progress)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.activity_currency_list)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun givenStateIsLoading_WhenActivityLoads_ThenLoadingStateIsDisplayed() {

        activityRule.runOnUiThread {
            testState.value = ViewState.Loading
        }

        onView(withId(R.id.activity_currency_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_currency_error_message)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.activity_currency_retry_button)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.activity_currency_list)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun givenStateIsReady_WhenActivityLoads_ThenReadyStateIsDisplayed() {

        activityRule.runOnUiThread {
            testState.value = ViewState.Ready(emptyList())
        }

        onView(withId(R.id.activity_currency_list)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_currency_progress)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.activity_currency_error_message)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.activity_currency_retry_button)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

}