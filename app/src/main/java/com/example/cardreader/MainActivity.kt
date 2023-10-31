package com.example.cardreader

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.EXTRA_ID
import android.nfc.NfcAdapter.EXTRA_TAG
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.math.E


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var readerAdapter: NfcAdapter
    private lateinit var cardTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardTextView = findViewById(R.id.cardNumber)

//      Setup NFC Adapter
        readerAdapter = NfcAdapter.getDefaultAdapter(this)

//      Start NFC service
        startService(Intent(this, CardHostApduService::class.java))
        Log.i("DRAGON", "We just started")
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(this, this.javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val intentFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val filters = arrayOf(intentFilter)

        val techList = arrayOf(arrayOf(NfcA::class.java.name))
        readerAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        readerAdapter.disableForegroundDispatch(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(EXTRA_TAG)
            val uid = bytesToHex(intent.getByteArrayExtra(EXTRA_ID))

            // Saving to Shared Preferences
            val sharedPrefs = getSharedPreferences("CardSimulation", Context.MODE_PRIVATE)
            sharedPrefs.edit().apply {
//                putString("tag", Gson().toJson(tag))
                putString("uid", uid)
            }.apply()

            //TODO: Savenut tento serial number a vytvorit simulaciu tejto karty pre terminal
            Log.i("DRAGON", "uid: $uid")
            cardTextView.text = "Serial Number: \n$uid"
            Toast.makeText(this, "Serial Number: $uid", Toast.LENGTH_LONG).show()
        }
    }
    // Helper function to convert bytes to a hexadecimal string
    private fun bytesToHex(bytes: ByteArray?): String {
        if(bytes == null) return "Couldn't read serial number from card"
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v shr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }
}