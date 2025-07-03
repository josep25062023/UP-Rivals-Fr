// En: network/dto/Tournament.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class Tournament(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("startDate")
    val startDate: String,

    @SerializedName("endDate")
    val endDate: String,

    @SerializedName("maxTeams")
    val maxTeams: Int,

    @SerializedName("status")
    val status: String
)