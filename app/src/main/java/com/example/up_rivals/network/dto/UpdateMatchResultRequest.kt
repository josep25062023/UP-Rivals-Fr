// En: network/dto/UpdateMatchResultRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateMatchResultRequest(
    @SerializedName("teamAScore")
    val teamAScore: Int,

    @SerializedName("teamBScore")
    val teamBScore: Int
)