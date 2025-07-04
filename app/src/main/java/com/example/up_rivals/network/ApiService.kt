// En: network/ApiService.kt
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
    suspend fun getMyTournaments(
        @Header("Authorization") token: String
    ): Response<List<Tournament>>

    @GET("tournaments/{id}")
    suspend fun getTournamentDetails(
        @Path("id") tournamentId: String
    ): Response<Tournament> // Esperamos recibir un solo objeto Tournament

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
}