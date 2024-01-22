package com.stepanov.maps

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY)
    }
}