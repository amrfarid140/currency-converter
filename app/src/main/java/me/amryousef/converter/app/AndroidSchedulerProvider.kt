package me.amryousef.converter.app

import kotlinx.coroutines.Dispatchers
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class AndroidSchedulerProvider @Inject constructor() : SchedulerProvider {
    override fun io() = Dispatchers.IO
    override fun main() = Dispatchers.Main
}