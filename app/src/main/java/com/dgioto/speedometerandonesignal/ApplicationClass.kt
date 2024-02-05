package com.dgioto.speedometerandonesignal

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

// NOTE: Replace the below with your own ONESIGNAL_APP_ID
const val ONESIGNAL_APP_ID = "8e4b6695-3a3a-4e08-8e19-268535b6b257"

class ApplicationCOnelass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Подробное ведение журнала помогает отлаживать проблемы. Удалите их перед выпуском приложения
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission отобразит собственный запрос на разрешение уведомлений Android
        // ПРИМЕЧАНИЕ. Вместо этого рекомендуется использовать внутриигровое сообщение OneSignal
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}