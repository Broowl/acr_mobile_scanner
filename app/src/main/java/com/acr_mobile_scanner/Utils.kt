package com.acr_mobile_scanner

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun strToDate(value: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(value)
}