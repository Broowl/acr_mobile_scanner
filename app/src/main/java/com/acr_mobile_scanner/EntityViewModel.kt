package com.acr_mobile_scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class EntityViewModel : ViewModel() {
    private val _scanner = MutableLiveData<Scanner>()
    val scanner: LiveData<Scanner> get() = _scanner

    fun initializeScanner(eventName:String, eventDate:Date, publicKey:String){
        val validator = SignatureValidator(publicKey)
        _scanner.value = Scanner(EventCharacteristics(eventName, eventDate), validator)
    }
}