// En: network/dto/StandingDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Representa una fila en la tabla de posiciones
data class StandingDto(
    @SerializedName("position")
    val position: Int,

    @SerializedName("team")
    val team: Team, // Reutilizamos el DTO de Equipo que ya tenemos

    @SerializedName("points")
    val points: Int,

    @SerializedName("gamesPlayed")
    val gamesPlayed: Int,

    @SerializedName("wins")
    val wins: Int,

    @SerializedName("losses")
    val losses: Int,

    @SerializedName("draws")
    val draws: Int
)