// En: network/dto/InscriptionRequestDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Este DTO representa una única solicitud de inscripción
data class InscriptionRequestDto(
    @SerializedName("id")
    val id: String, // El ID de la inscripción en sí

    @SerializedName("status")
    val status: String, // "pending", "approved", etc.

    @SerializedName("tournament")
    val tournament: Tournament, // Objeto anidado con los datos del torneo

    @SerializedName("team")
    val team: Team // Objeto anidado con los datos del equipo
)