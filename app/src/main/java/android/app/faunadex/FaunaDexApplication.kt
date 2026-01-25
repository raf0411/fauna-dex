package android.app.faunadex

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FaunaDexApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("FaunaDexApp", "onCreate: Application starting...")
    }
}
