package com.automation.common.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryMonitor(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun getBatteryPercentage(): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun isCharging(): Boolean {
        val status = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return status?.let { intent ->
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                    plugged == BatteryManager.BATTERY_PLUGGED_USB ||
                    plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        } ?: false
    }

    fun registerBatteryStatusReceiver(onBatteryStatusChanged: (Int, Boolean) -> Unit) {
        val batteryStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
                    onBatteryStatusChanged(level, isCharging)
                }
            }
        }

        context.registerReceiver(
            batteryStatusReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }
}