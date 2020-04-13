package com.pembelajar.phisicalalert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class SearchDevices : BroadcastReceiver() {
    companion object{
        var ID_NOTIF = 1
        var CHANNEL_ID = "Notif_01"
        var CHANNEL_NAME = "Notif_Alert"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when(action){
            BluetoothDevice.ACTION_FOUND -> {
                val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                if (rssi > -80){
                    showNotif(context)
                }else{
                    Log.d("DEBUG", "RSSI NYA LEBIH = $rssi")
                }
            }
        }
    }

    private fun showNotif(context: Context){
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alert)
            .setSound(sound)
            .setContentTitle("PERINGATAN !!!")
            .setContentText("Jaga Jarak Dengan Orang Sekitar")
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notifChannel.enableVibration(true)
            notifChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)

            builder.setChannelId(CHANNEL_ID)

            notifManager.createNotificationChannel(notifChannel)
        }

        val notify = builder.build()
        notifManager.notify(ID_NOTIF, notify)
    }
}
