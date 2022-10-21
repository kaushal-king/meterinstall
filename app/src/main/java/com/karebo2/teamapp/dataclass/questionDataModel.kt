package com.karebo2.teamapp.dataclass

import com.google.gson.annotations.SerializedName

class questionDataModel (
    @SerializedName("id")
    val id: Int,
    @SerializedName("questions")
    val questions: String,
    @SerializedName("value")
    var value: Boolean = false,
)