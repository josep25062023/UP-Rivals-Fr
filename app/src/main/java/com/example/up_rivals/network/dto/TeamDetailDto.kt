// En: network/dto/TeamDetailDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Contiene los detalles completos de un equipo
data class TeamDetailDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String?,

    // Incluye una lista de los miembros del equipo
    @SerializedName("members")
    val members: List<User> // Reutilizamos el DTO de User
)