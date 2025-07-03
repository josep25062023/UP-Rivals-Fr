// En: network/dto/Team.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class Team(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String? // El logo puede ser opcional
)