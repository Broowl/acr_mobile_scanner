package com.acr_mobile_scanner

import androidx.lifecycle.ViewModel
import java.util.Date

class ConfigurationViewModel : ViewModel() {
    private var _name: String? = null
    private var _date: Date? = null

    val name: String? get() = _name
    val date: Date? get() = _date

    fun set(name: String, date: Date) {
        _name = name
        _date = date
    }
}