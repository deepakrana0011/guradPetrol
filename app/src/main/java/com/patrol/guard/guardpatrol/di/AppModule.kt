package com.patrol.guard.guardpatrol.di

import android.app.Application
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.*
import com.patrol.guard.guardpatrol.view.GuardPatrolApplication
import com.patrol.guard.guardpatrol.viewModel.*
import org.koin.android.viewmodel.dsl.viewModel


import org.koin.core.module.Module
import org.koin.dsl.module


object AppModule {
    fun appModule(application: GuardPatrolApplication): Module = module {
        single { FrequentFunctions() }
        single { FragmentFunctions() }
        single{ WebServices(application, get()) }
        single{ BasicFunctions() }
        single { SharedPref(application) }
        single { DateFunctions() }
        viewModel { HomeViewModel(get(),get(),get()) }
        viewModel { IncidentActivityViewModel(get(),get(),get()) }
        viewModel { ScanViewModel(get(),get()) }
        viewModel{LoginViewModel(application, get(),get(),get())}
        viewModel{MessageActivityViewModel(get(),get(),get())}
        viewModel{TimelineViewModel(get(),get(),get())}
    }
}