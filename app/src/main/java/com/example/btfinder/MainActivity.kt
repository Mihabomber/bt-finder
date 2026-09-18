package com.example.btfinder

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
  private var scanner: BluetoothLeScanner? = null
  private var scanning = false
  private lateinit var tvStatus: TextView
  private lateinit var tvDist: TextView
  private lateinit var tvRssi: TextView
  private lateinit var etTarget: EditText
  private lateinit var btn: Button

  private val callback = object : ScanCallback() {
    override fun onScanResult(type: Int, result: ScanResult) {
      val name = result.device.name ?: result.scanRecord?.deviceName ?: "Unknown"
      val filter = etTarget.text.toString().trim()
      if (filter.isNotEmpty() && !name.contains(filter, true)) return
      val rssi = result.rssi
      val dist = rssiToMeters(rssi)
      runOnUiThread {
        tvStatus.text = "Найден: $name"
        tvRssi.text = "RSSI: $rssi dBm"
        tvDist.text = "Дистанция: %.1f м %s".format(dist, proximity(dist))
      }
    }
  }

  fun rssiToMeters(rssi: Int, txPower: Int = -59, n: Double = 2.0): Double =
    10.0.pow((txPower - rssi) / (10 * n))

  fun proximity(d: Double) = when {
    d < 1 -> "🔥 ОЧЕНЬ БЛИЗКО"; d < 3 -> "🟢 Близко"
    d < 10 -> "🟡 Средне"; else -> "🔵 Далеко"
  }

  override fun onCreate(s: Bundle?) {
    super.onCreate(s)
    setContentView(R.layout.activity_main)
    tvStatus = findViewById(R.id.tvStatus); tvDist = findViewById(R.id.tvDist)
    tvRssi = findViewById(R.id.tvRssi); etTarget = findViewById(R.id.etTarget)
    btn = findViewById(R.id.btnToggle)
    btn.setOnClickListener { if (scanning) stop() else start() }
    ActivityCompat.requestPermissions(this,
      arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION), 1)
  }

  private fun start() {
    val mgr = getSystemService(BluetoothManager::class.java)
    val adapter: BluetoothAdapter? = mgr.adapter
    if (adapter == null || !adapter.isEnabled) {
      Toast.makeText(this, "Включи Bluetooth", Toast.LENGTH_SHORT).show(); return
    }
    scanner = adapter.bluetoothLeScanner
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
      Toast.makeText(this, "Нет разрешения BLUETOOTH_SCAN", Toast.LENGTH_SHORT).show(); return
    }
    scanner?.startScan(buildSettings(), buildFilters(), callback)
    scanning = true; btn.text = "ВЫКЛЮЧИТЬ"
    tvStatus.text = "Сканирую..."
  }
  private fun stop() {
    try {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
        scanner?.stopScan(callback)
    } catch (_: Exception) {}
    scanning = false; btn.text = "ВКЛЮЧИТЬ ПОИСК"; tvStatus.text = "Остановлено"
  }
  private fun buildSettings() = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
  private fun buildFilters(): List<ScanFilter> = emptyList()
  override fun onDestroy() { super.onDestroy(); stop() }
}
