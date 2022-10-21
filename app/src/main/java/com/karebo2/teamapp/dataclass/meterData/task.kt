package com.karebo2.teamapp.dataclass.meterData

data class task(
    val cardType: String,
    val hideInInbox: Boolean,
    val jobCardId: String,
    val latitude: Double,
    val longitude: Double,
    val municipality: String,
    val parcelAddress: String,
    val postedOn: String,
    val project: String,
    val sgCode: String,
    val subJobCardIds: Any,
    val subJobCards: Any,
    val team: String,
    val vertices: List<Any>
)