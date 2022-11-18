package io.cosmostation.sui.sample

import android.app.Application
import io.cosmostation.suikotlin.SuiClient

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SuiClient.initialize(this)
    }
}