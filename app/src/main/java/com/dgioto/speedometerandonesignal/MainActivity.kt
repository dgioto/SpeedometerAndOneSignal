package com.dgioto.speedometerandonesignal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dgioto.speedometerandonesignal.databinding.ActivityMainBinding
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// NOTE: Replace the below with your own ONESIGNAL_APP_ID
const val ONESIGNAL_APP_ID = "8e4b6695-3a3a-4e08-8e19-268535b6b257"

class MainActivity : AppCompatActivity(), CustomView.Listener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.customView.listener = this

        setOneSignalTest()
    }

    override fun onClick(index: Int) {
        binding.tvSelection.text = TextUtils.menuList[index]
    }

    private fun setOneSignalTest() {
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}
