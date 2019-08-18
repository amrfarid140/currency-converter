package me.amryousef.converter.app

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.amryousef.converter.domain.SchedulerProvider
import javax.inject.Inject

class AndroidSchedulerProvider @Inject constructor() : SchedulerProvider {
    override fun io() = Schedulers.io()
    override fun main(): Scheduler = AndroidSchedulers.mainThread()
}