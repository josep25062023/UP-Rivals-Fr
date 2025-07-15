package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class PendingMatchDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("teamA")
    val teamA: MatchTeamDto,

    @SerializedName("teamB")
    val teamB: MatchTeamDto,

    @SerializedName("tournament")
    val tournament: MatchTournamentDto
)

data class MatchTournamentDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)