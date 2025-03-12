package com.wynndie.sendytestapp

import android.app.Application
import com.wynndie.sendytestapp.di.AppModule

class App : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule()
    }
}