package com.acr_mobile_scanner

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.io.File

class IdStorage(private val _filesDir: String) {
    private var _eventCharacteristics: EventCharacteristics? = null
    private var _file: File? = null
    private val _storage: MutableSet<UInt> = HashSet()


    fun tryAddId(id: UInt): Boolean {
        if (_storage.contains(id)) {
            return false
        }
        add(id)
        return true
    }

    fun setEventCharacteristics(characteristics: EventCharacteristics) {
        _eventCharacteristics = characteristics
        saveOpen()
    }

    private fun add(id: UInt) {
        val file = _file ?: return
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timeStamp = formatter.format(time)
        _storage.add(id)
        file.writer().append("${timeStamp},${id}\n")
        file.writer().flush()
    }

    private fun saveOpen() {
        val fileName = getFileName() ?: return
        val file = File(_filesDir, fileName)
        if (file.exists()) // todo: check file age
        {
            _file = file
            readFile()
        } else {
            file.createNewFile()
            _file = file
        }
    }

    private fun getFileName(): String? {
        val eventCharacteristics = _eventCharacteristics ?: return null
        val dateString = SimpleDateFormat("yyyy-MM-dd").format(eventCharacteristics.date)
        return "${dateString}_${eventCharacteristics.name.replace(' ', '_')}.csv"
    }

    private fun readFile() {
        val matcher = Regex(".*,(.*)")
        _file?.bufferedReader()?.useLines { lines ->
            lines.map { line ->
                val match = matcher.find(line)?.groupValues?.get(1)
                if (match != null) {
                    _storage.add(match.toUInt())
                }
            }
        }
    }
}