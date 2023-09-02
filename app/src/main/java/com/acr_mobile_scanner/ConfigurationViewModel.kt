package com.acr_mobile_scanner

import androidx.lifecycle.ViewModel
import java.util.Date

class ConfigurationViewModel : ViewModel() {
    private var _name: String? = null
    private var _date: Date? = null
    private var _advancedTicked: Boolean? = null

    var name: String?
        get() = _name
        set(name) {
            _name = name
        }
    var date: Date?
        get() = _date
        set(date) {
            _date = date
        }
    var advancedTicked: Boolean?
        get() = _advancedTicked
        set(advancedTicked) {
            _advancedTicked = advancedTicked
        }
}