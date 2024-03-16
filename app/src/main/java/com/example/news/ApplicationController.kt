package com.example.news

import android.app.Application

class ApplicationController : Application() {

    companion object {
        var INSTANCE: ApplicationController? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
    }
}