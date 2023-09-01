package com.acr_mobile_scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class EntityViewModel : ViewModel() {
    private var _scanner: Scanner? = null
    private var _idStorage: IdStorage? = null
    val scanner: Scanner? get() = _scanner

    fun setLogDir(logDir: String) {
        _idStorage = IdStorage(logDir)
    }

    fun initializeScanner(eventName: String, eventDate: Date, publicKey: String) {
        val idStorage = _idStorage ?: return
        val validator = SignatureValidator(publicKey)
        val characteristics = EventCharacteristics(eventName, eventDate)
        idStorage.setEventCharacteristics(characteristics)
        _scanner = Scanner(characteristics, validator, idStorage)
    }
}