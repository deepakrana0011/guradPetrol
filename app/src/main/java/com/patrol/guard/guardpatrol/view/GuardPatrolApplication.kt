package com.patrol.guard.guardpatrol.view

import android.app.Application
import com.patrol.guard.guardpatrol.di.AppModule
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.LocationInternetFunctions
import org.koin.core.context.startKoin

class GuardPatrolApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationInternetFunctions()
        startKoin {
            modules(AppModule.appModule(this@GuardPatrolApplication))
        }
    }
}