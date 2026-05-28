package com.pv.sales

import android.app.Application
import com.pv.sales.data.database.AppDatabase

class PVApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: PVApplication
            private set
    }
}
