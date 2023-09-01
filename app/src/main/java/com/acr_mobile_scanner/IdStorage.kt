package com.acr_mobile_scanner

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class IdStorage(private val _filesDir: String) {
    private var _eventCharacteristics: EventCharacteristics? = null
    private var _fileWriter: BufferedWriter? = null
    private var _filePath: String? = null
    private val _storage: MutableSet<UInt> = HashSet()


    fun tryAddId(id: UInt): Boolean {
        if (_storage.contains(id)) {
            return false
        }
        add(id)
        return true
    }

    fun setEventCharacteristics(characteristics: EventCharacteristics) {
        _fileWriter?.close()
        _eventCharacteristics = characteristics
        saveOpen()
    }

    private fun add(id: UInt) {
        val fileWriter = _fileWriter ?: return
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timeStamp = formatter.format(time)
        _storage.add(id)
        fileWriter.appendLine("${timeStamp},${id}")
        fileWriter.flush()
    }

    private fun saveOpen() {
        val fileName = getFileName() ?: return
        val logDir = File(_filesDir, "logs")
        logDir.mkdirs()
        val file = File(logDir, fileName)
        if (file.exists()) {
            _fileWriter = BufferedWriter(FileWriter(file.absolutePath, true))
            _filePath = file.absolutePath
            readFile()
        } else {
            file.createNewFile()
            _fileWriter = BufferedWriter(FileWriter(file.absolutePath, true))
            _filePath = file.absolutePath
        }
    }

    private fun getFileName(): String? {
        val eventCharacteristics = _eventCharacteristics ?: return null
        val dateString = SimpleDateFormat("yyyy-MM-dd").format(eventCharacteristics.date)
        return "${dateString}_${eventCharacteristics.name.replace(' ', '_')}.csv"
    }

    private fun readFile() {
        val fileReader = BufferedReader(FileReader(_filePath))
        val matcher = Regex(".*,(.*)")
        val lines = fileReader.readText().split('\n')
        lines.map { line ->
            val match = matcher.find(line)?.groupValues?.get(1)
            if (match != null) {
                _storage.add(match.toUInt())
            }
        }
    }
}