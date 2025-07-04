// En: network/dto/MatchDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Representa un único partido
data class MatchDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("matchDate")
    val matchDate: String, // La fecha y hora del partido en formato ISO (ej. "2025-08-10T14:00:00Z")

    @SerializedName("status")
    val status: String, // Podría ser "pending" o "finished"

    @SerializedName("teamA")
    val teamA: Team, // Objeto anidado para el equipo A

    @SerializedName("teamB")
    val teamB: Team, // Objeto anidado para el equipo B

    @SerializedName("scoreA")
    val scoreA: Int?, // Marcador del equipo A (puede ser nulo si no se ha jugado)

    @SerializedName("scoreB")
    val scoreB: Int? // Marcador del equipo B (puede ser nulo si no se ha jugado)
)