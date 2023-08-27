package com.acr_mobile_scanner

import java.net.URLDecoder
import java.util.Date

data class DecodeResult(
    val encoded: String,
    val eventName: String,
    val eventDate: Date,
    val ticketId: Int,
    val signature: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecodeResult

        if (encoded != other.encoded) return false
        if (eventName != other.eventName) return false
        if (eventDate != other.eventDate) return false
        if (ticketId != other.ticketId) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encoded.hashCode()
        result = 31 * result + eventName.hashCode()
        result = 31 * result + eventDate.hashCode()
        result = 31 * result + ticketId
        result = 31 * result + signature.contentHashCode()
        return result
    }
}

fun unquoteToBytes(value: String): ByteArray {
    val unquoted = URLDecoder.decode(value, "UTF-8").toByteArray()
    return android.util.Base64.decode(unquoted, android.util.Base64.DEFAULT)
}

fun decodeMessage(data: String): DecodeResult? {
    var matcher = Regex("^((.*)_([-\\d]+)_(\\d+))__(.*)\$")
    var matches: MatchResult = matcher.matchAt(data, 0) ?: return null
    var groups = matches.groupValues
    var encoded = groups[1]
    var eventName = groups[2]
    var eventDate = strToDate(groups[3])
    var ticketId = groups[4].toInt()
    var signature = unquoteToBytes(groups[5]) ?: return null
    return DecodeResult(encoded, eventName, eventDate, ticketId, signature)
}