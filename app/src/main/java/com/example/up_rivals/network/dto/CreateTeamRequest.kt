// En: network/dto/CreateTeamRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class CreateTeamRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String? = null // El logo es opcional
)