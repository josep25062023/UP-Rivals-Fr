// En: network/dto/UpdateTeamRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateTeamRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("logo")
    val logo: String? = null
)