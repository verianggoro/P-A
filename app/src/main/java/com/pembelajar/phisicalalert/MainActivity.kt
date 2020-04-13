package com.pembelajar.phisicalalert

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pembelajar.phisicalalert.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var doubleBack = false
    private var broadcastReceiver = SearchDevices()

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device Not Support", Toast.LENGTH_LONG).show()
            return
        } else {
            if (!bluetoothAdapter!!.isEnabled) {
                enabledBluetooth()
            } else {
                binding.statusBluetooth.text = "Bluetooth Menyala"
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(broadcastReceiver, filter)

    }

    override fun onResume() {
        super.onResume()
        scanDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (doubleBack){
                super.onBackPressed()
            }
            doubleBack = true
            Toast.makeText(this, "Layanan Akan Berhenti", Toast.LENGTH_SHORT).show()
            Handler().postDelayed(Runnable { doubleBack = false }, 1000)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun enabledBluetooth() {
        val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            when(resultCode){
                Activity.RESULT_OK -> {
                    binding.statusBluetooth.text = "Bluetooth Menyala"
                    scanDevices()
                    }
                Activity.RESULT_CANCELED -> {finish()}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck =
                checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1001
                )
            }
        } else {
            Log.d(
                "FragmentActivity.TAG",
                "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP."
            )
        }
    }

    private fun scanDevices(){
        if (bluetoothAdapter!!.isEnabled) {
            checkBTPermissions()
            bluetoothAdapter!!.startDiscovery()
        }
    }
}
