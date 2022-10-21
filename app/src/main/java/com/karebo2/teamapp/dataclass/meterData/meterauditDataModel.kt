package com.karebo2.teamapp.dataclass.meterData

import com.google.gson.annotations.SerializedName

data class meterauditDataModel(
    val cardType: String? = null,
    val hideInInbox: Boolean? = null,
    val jobCardId: String? = null,
    @SerializedName("longitude")
    val latitude: Double? = null,
    @SerializedName("latitude")
    val longitude: Double? = null,
    val municipality: String? = null,
    val parcelAddress: String? = null,
    val postedOn: String? = null,
    val project: String? = null,
    val sgCode: String? = null,
    val subJobCardIds: List<String>? = null,
    val subJobCards: List<SubJobCard>? = null,
    val team: String? = null,
    val vertices: List<String>? = null
)