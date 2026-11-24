package com.example.appdeviajes.data.remote.publicholiday

import com.google.gson.annotations.SerializedName

data class PublicHolidayDto(
    @SerializedName("date") val date: String,
    @SerializedName("localName") val localName: String,
    @SerializedName("name") val name: String,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("fixed") val fixed: Boolean,
    @SerializedName("global") val global: Boolean,
    @SerializedName("counties") val counties: List<String>?,
    @SerializedName("launchYear") val launchYear: Int?,
    @SerializedName("types") val types: List<String>?
)

data class LongWeekendDto(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("dayCount") val dayCount: Int,
    @SerializedName("needBridgeDay") val needBridgeDay: Boolean,
    @SerializedName("bridgeDays") val bridgeDays: List<String>?
)
