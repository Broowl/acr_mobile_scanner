package com.acr_mobile_scanner


class Scanner constructor(val eventCharacteristics: EventCharacteristics) {

    fun process_qr_code(payload: String): Unit {
        var decodeResult = decodeMessage(payload) ?: return
        if (decodeResult.eventName != eventCharacteristics.name) {
            return
        }
        if (decodeResult.eventDate != eventCharacteristics.date){
            return
        }

    }
}