package com.moksh.deskos

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var savedBrightness = 0.35f
    private var isCharging = false
    private var userKeepAwake = true
    private var chargingOnly = false
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            applyKeepAwake()
            if (::web.isInitialized) web.evaluateJavascript("updateBattery(${batteryPercent()})", null)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        web = WebView(this)
        web.setBackgroundColor(android.graphics.Color.BLACK)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = false
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.addJavascriptInterface(DeskBridge(), "DeskOS")
        setContentView(web)
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        web.loadUrl("file:///android_asset/index.html")
    }

    private fun applyKeepAwake() {
        val enabled = userKeepAwake && (!chargingOnly || isCharging)
        if (enabled) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun batteryPercent(): Int {
        val manager = getSystemService(BATTERY_SERVICE) as BatteryManager
        return manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
    }

    inner class DeskBridge {
        @JavascriptInterface fun setPowerPolicy(keepAwake: Boolean, onlyWhileCharging: Boolean) {
            runOnUiThread { userKeepAwake = keepAwake; chargingOnly = onlyWhileCharging; applyKeepAwake() }
        }
        @JavascriptInterface fun getBatteryPercent(): Int = batteryPercent()
        @JavascriptInterface fun setBrightness(value: Float) {
            runOnUiThread {
                val p = window.attributes
                p.screenBrightness = max(0.02f, min(1f, value))
                window.attributes = p
            }
        }
        @JavascriptInterface fun sleepDisplay() {
            runOnUiThread {
                val p = window.attributes
                if (p.screenBrightness >= 0f) savedBrightness = p.screenBrightness
                p.screenBrightness = 0.02f
                window.attributes = p
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        @JavascriptInterface fun wakeDisplay() {
            runOnUiThread {
                val p = window.attributes
                p.screenBrightness = if (savedBrightness > 0f) savedBrightness else 0.35f
                window.attributes = p
                applyKeepAwake()
            }
        }
    }

    override fun onDestroy() {
        try { unregisterReceiver(batteryReceiver) } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBackPressed() {
        web.evaluateJavascript("closeTopPanel()", null)
    }
}
