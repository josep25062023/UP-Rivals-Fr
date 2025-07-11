// En: network/dto/MatchDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Versión final y correcta basada en tu Postman
data class MatchDto(
    @SerializedName("id")
    val id: String,

    // CAMBIADO: El campo se llama 'date'
    @SerializedName("date")
    val matchDate: String,

    @SerializedName("status")
    val status: String,

    // El servidor devuelve un objeto de equipo más complejo con captain
    @SerializedName("teamA")
    val teamA: MatchTeamDto,

    @SerializedName("teamB")
    val teamB: MatchTeamDto,

    // CAMBIADO: Los campos de marcador tienen nombres diferentes
    @SerializedName("teamAScore")
    val scoreA: Int?,

    @SerializedName("teamBScore")
    val scoreB: Int?
)