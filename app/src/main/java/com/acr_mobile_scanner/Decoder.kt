package com.acr_mobile_scanner

import java.net.URLDecoder
import java.util.Date

data class DecodeResult(
    val encoded: String,
    val eventName: String,
    val eventDate: Date,
    val ticketId: Int,
    val signature: String
)

fun unquoteToBytes(value: String): String? {
    return URLDecoder.decode(value, "UTF-8")
}

fun decodeMessage(data: String): DecodeResult? {
    var matcher = Regex("^((.*)_([-\\d]+)_(\\d+))__(.*)\$")
    var matches: MatchResult = matcher.matchAt(data, 0) ?: return null
    var groups = matches.groupValues
    var encoded = groups[0]
    var eventName = groups[1]
    var eventDate = strToDate(groups[2])
    var ticketId = groups[3].toInt()
    var signature = unquoteToBytes(groups[4]) ?: return null
    return DecodeResult(encoded, eventName, eventDate, ticketId, signature)
}