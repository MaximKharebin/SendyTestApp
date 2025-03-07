package com.wynndie.sendytestapp

import android.app.Application

class App : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}