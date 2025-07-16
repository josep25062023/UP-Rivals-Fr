package com.example.up_rivals.network

import com.example.up_rivals.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Rutas de Autenticación ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<User>

    @PATCH("auth/{id}")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body request: UpdateProfileRequest
    ): Response<User>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    // --- Rutas de Torneos ---
    @POST("tournaments")
    suspend fun createTournament(
        @Header("Authorization") token: String,
        @Body request: CreateTournamentRequest
    ): Response<Tournament>

    @GET("tournaments")
    suspend fun getTournaments(): Response<List<Tournament>>

    @GET("tournaments/my-tournaments")
    suspend fun getMyTournaments(@Header("Authorization") token: String): Response<List<Tournament>>

    @GET("tournaments/{id}")
    suspend fun getTournamentDetails(
        @Header("Authorization") token: String,
        @Path("id") tournamentId: String
    ): Response<Tournament>

    // Nuevo endpoint público para visitantes
    @GET("tournaments/{id}/public")
    suspend fun getTournamentDetailsPublic(
        @Path("id") tournamentId: String
    ): Response<Tournament>

    @GET("tournaments/{id}/standings")
    suspend fun getTournamentStandings(
        @Path("id") tournamentId: String
    ): Response<List<StandingDto>>

    @GET("tournaments/{id}/matches")
    suspend fun getTournamentMatches(
        @Path("id") tournamentId: String
    ): Response<List<MatchDto>>

    // --- FUNCIÓN QUE FALTABA ---
    @GET("player/my-tournaments")
    suspend fun getPlayerMyTournaments(
        @Header("Authorization") token: String
    ): Response<List<Tournament>>

    // --- Rutas de Inscripciones ---
    @PATCH("tournaments/{tournamentId}/inscriptions/{teamId}")
    suspend fun updateInscriptionStatus(
        @Header("Authorization") token: String,
        @Path("tournamentId") tournamentId: String,
        @Path("teamId") teamId: String,
        @Body statusUpdate: UpdateInscriptionRequest
    ): Response<Unit>

    @GET("organizer/inscriptions")
    suspend fun getOrganizerInscriptions(
        @Header("Authorization") token: String
    ): Response<List<InscriptionRequestDto>>

    // --- Rutas de Equipos ---
    @GET("teams/{id}")
    suspend fun getTeamDetails(
        @Header("Authorization") token: String,
        @Path("id") teamId: String
    ): Response<TeamDetailDto>

    @POST("teams/{id}/members")
    suspend fun addTeamMember(
        @Header("Authorization") token: String,
        @Path("id") teamId: String,
        @Body request: AddMemberRequest
    ): Response<Unit>

    @POST("teams")
    suspend fun createTeam(
        @Header("Authorization") token: String,
        @Body request: CreateTeamRequest
    ): Response<Team>

    @POST("tournaments/{tournamentId}/inscribe/{teamId}")
    suspend fun inscribeTeam(
        @Header("Authorization") token: String,
        @Path("tournamentId") tournamentId: String,
        @Path("teamId") teamId: String
    ): Response<Unit>

    @DELETE("tournaments/{id}")
    suspend fun deleteTournament(
        @Header("Authorization") token: String,
        @Path("id") tournamentId: String
    ): Response<Unit> // No esperamos datos de vuelta, solo una confirmación

    // --- FUNCIÓN AÑADIDA ---
    @GET("player/teams")
    suspend fun getPlayerTeams(
        @Header("Authorization") token: String
    ): Response<List<PlayerTeamDto>> // Esperamos una lista de nuestro nuevo DTO

    @POST("tournaments/{id}/generate-schedule")
    suspend fun generateSchedule(
        @Header("Authorization") token: String,
        @Path("id") tournamentId: String
    ): Response<Unit>

    // --- Rutas de Partidos ---
    @PATCH("matches/{id}/result")
    suspend fun updateMatchResult(
        @Header("Authorization") token: String,
        @Path("id") matchId: String,
        @Body request: UpdateMatchResultRequest
    ): Response<MatchDto>

    // --- Rutas de Actividades ---
    @GET("organizer/pending-matches")
    suspend fun getPendingMatches(
        @Header("Authorization") token: String
    ): Response<List<PendingMatchDto>>

    @GET("player/pending-matches")
    suspend fun getPlayerPendingMatches(
        @Header("Authorization") token: String
    ): Response<List<PendingMatchDto>>
}