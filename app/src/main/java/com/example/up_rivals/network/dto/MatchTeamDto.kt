// En: network/dto/MatchTeamDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// DTO específico para equipos en matches que incluye el captain
data class MatchTeamDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String?,

    @SerializedName("captain")
    val captain: User
)