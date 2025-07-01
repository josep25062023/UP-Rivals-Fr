// En: network/dto/RegisterRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("role")
    val role: String // "player" o "organizer"
)