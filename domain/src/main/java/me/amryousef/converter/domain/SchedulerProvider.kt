package me.amryousef.converter.domain

import kotlinx.coroutines.CoroutineDispatcher

interface SchedulerProvider {
    fun io(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
}