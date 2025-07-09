// En: network/dto/PlayerTeamDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class PlayerTeamDto(
    @SerializedName("teamId")
    val teamId: String,

    @SerializedName("teamName")
    val teamName: String,

    @SerializedName("teamLogo")
    val teamLogo: String?,

    @SerializedName("tournament")
    val tournament: TournamentInfo // Usamos el DTO que acabamos de crear
)