package com.example.appdeviajes.data.remote.ipgeolocation

import com.google.gson.annotations.SerializedName

data class IpGeolocationDto(
    @SerializedName("ip") val ip: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("country_name") val countryName: String?,
    @SerializedName("region_name") val regionName: String?,
    @SerializedName("city_name") val cityName: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("zip_code") val zipCode: String?,
    @SerializedName("time_zone") val timeZone: String?,
    @SerializedName("is_proxy") val isProxy: Boolean?
)
