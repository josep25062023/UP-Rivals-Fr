// En: network/dto/Tournament.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Versión final y definitiva del DTO
data class Tournament(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("modality")
    val modality: String,

    @SerializedName("maxTeams")
    val maxTeams: Int,

    @SerializedName("startDate")
    val startDate: String,

    @SerializedName("endDate")
    val endDate: String,

    // CORREGIDO: Usamos 'rules' como nos muestra Postman
    @SerializedName("rules")
    val rules: String,

    @SerializedName("status")
    val status: String

    // El objeto 'organizer' lo ignoramos por ahora para no complicar el molde,
    // ya que no lo estamos usando en la UI de detalle.
)