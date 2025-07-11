// En: network/dto/TeamDetailDto.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Representa la respuesta completa de /teams/:id
data class TeamDetailDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String?,

    @SerializedName("captain")
    val captain: User,

    // Corregido: Ahora la lista es de TeamMemberDto
    @SerializedName("members")
    val members: List<TeamMemberDto>,

    @SerializedName("inscriptions")
    val inscriptions: List<InscriptionDto>
)

// Representa a un miembro dentro de la lista "members"
data class TeamMemberDto(
    // No necesitamos userId ni teamId aquí, solo el objeto 'user'
    @SerializedName("user")
    val user: User
)

// Representa una inscripción dentro de la lista "inscriptions"
data class InscriptionDto(
    @SerializedName("status")
    val status: String,

    @SerializedName("tournament")
    val tournament: Tournament
)

