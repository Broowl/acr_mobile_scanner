package com.acr_mobile_scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class EntityViewModel : ViewModel() {
    private val _scanner = MutableLiveData<Scanner>()
    private val _idStorage = MutableLiveData<IdStorage>()
    val scanner: LiveData<Scanner> get() = _scanner

    fun setLogDir(logDir: String) {
        _idStorage.value = IdStorage(logDir)
    }

    fun initializeScanner(eventName: String, eventDate: Date, publicKey: String) {
        val idStorage = _idStorage.value ?: return
        val validator = SignatureValidator(publicKey)
        val characteristics = EventCharacteristics(eventName, eventDate)
        idStorage.setEventCharacteristics(characteristics)
        _scanner.value = Scanner(characteristics, validator, idStorage)
    }
}