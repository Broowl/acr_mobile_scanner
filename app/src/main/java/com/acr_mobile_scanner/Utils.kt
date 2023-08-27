package com.acr_mobile_scanner

import java.text.SimpleDateFormat
import java.util.Date

fun strToDate(value: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM.dd")
    return formatter.parse(value)
}