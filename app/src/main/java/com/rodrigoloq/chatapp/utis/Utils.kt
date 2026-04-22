package com.rodrigoloq.chatapp.utis

import android.text.format.DateFormat
import kotlinx.coroutines.delay
import java.util.Arrays
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class Utils {
    val MESSAGE_TYPE_TEXT = "TEXTO"
    val MESSAGE_TYPE_IMAGE = "IMAGEN"
    fun getDeviceTime() : Long {
        return System.currentTimeMillis()
    }
    fun dateFormater(time: Long, withHour: Boolean): String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = time
        var format = ""

        if(withHour) format = "dd/MM/yyyy HH:ss" else format = "dd/MM/yyyy"

        return DateFormat.format(format, calendar).toString()
    }
    fun chatRoute(receptorUid: String, emisorUid: String): String{
        val arrayUid = arrayOf(receptorUid,emisorUid)
        Arrays.sort(arrayUid)
        return "${arrayUid[0]}_${arrayUid[1]}"
    }
}