// En: network/dto/CreateTournamentRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class CreateTournamentRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("modality")
    val modality: String,

    @SerializedName("maxTeams")
    val maxTeams: Int,

    @SerializedName("startDate")
    val startDate: String, // Formato "YYYY-MM-DDTHH:mm:ssZ"

    @SerializedName("endDate")
    val endDate: String, // Formato "YYYY-MM-DDTHH:mm:ssZ"

    @SerializedName("rules")
    val rules: String
)