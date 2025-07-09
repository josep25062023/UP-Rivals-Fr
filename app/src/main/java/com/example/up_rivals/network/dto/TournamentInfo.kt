// En: network/dto/TournamentInfo.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class TournamentInfo(
    @SerializedName("tournamentId")
    val tournamentId: String,

    @SerializedName("tournamentName")
    val tournamentName: String
)