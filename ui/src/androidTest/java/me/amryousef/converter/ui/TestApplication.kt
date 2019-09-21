package me.amryousef.converter.ui

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

class TestApplication : Application(), HasAndroidInjector {
    lateinit var dispatchingAndroidInjector: AndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}