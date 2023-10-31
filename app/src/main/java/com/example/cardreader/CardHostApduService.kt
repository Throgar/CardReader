package com.example.cardreader

import android.nfc.Tag
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class CardHostApduService: HostApduService() {

    private lateinit var tag: Tag
    private var uid: String? = ""

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }*/

    override fun onCreate() {
        Log.i("DRAGON", "Started Service")
        val sharedPrefs = getSharedPreferences("CardSimulation", MODE_PRIVATE)
        try {
//            tag = Gson().fromJson(sharedPrefs.getString("tag", ""), Tag::class.java)
            uid = sharedPrefs.getString("uid", "")
            Log.i("DRAGON", "UID: $uid")
        } catch (e: Exception) {
            Log.i("DRAGON", "No tag saved in SharedPreferences")
        }

        super.onCreate()
    }

    override fun processCommandApdu(commandsApdu: ByteArray?, extras: Bundle?): ByteArray {
        Toast.makeText(this.application, commandsApdu.toString(), Toast.LENGTH_LONG)
            .show()
        Log.i("DRAGON", commandsApdu.toString())
        return commandsApdu!!
    }

    override fun onDeactivated(p0: Int) {
        Log.i("DRAGON", p0.toString())
    }

}