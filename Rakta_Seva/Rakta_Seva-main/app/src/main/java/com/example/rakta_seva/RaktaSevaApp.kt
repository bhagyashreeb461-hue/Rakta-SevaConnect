package com.example.rakta_seva

import android.app.Application

class RaktaSevaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
    }
}