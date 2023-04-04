package io.cosmostation.sui.sample

import android.app.Application
import io.cosmostation.suikotlin.SuiClient
import io.cosmostation.suikotlin.model.Network

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SuiClient.initialize()
        SuiClient.configure(Network.Testnet())
    }
}