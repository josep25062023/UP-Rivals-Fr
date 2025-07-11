// En: network/dto/User.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("profilePicture")
    val profilePicture: String?, // Cambiado de profilePic a profilePicture
    @SerializedName("institution")
    val institution: String?,
    @SerializedName("career")
    val career: String?,
    @SerializedName("isActive")
    val isActive: Boolean
)