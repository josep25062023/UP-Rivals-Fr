package com.example.up_rivals.network // Asegúrate que el nombre del paquete coincida

import com.example.up_rivals.network.dto.CreateTournamentRequest
import com.example.up_rivals.network.dto.LoginRequest
import com.example.up_rivals.network.dto.LoginResponse
import com.example.up_rivals.network.dto.RegisterRequest
import com.example.up_rivals.network.dto.Tournament
import com.example.up_rivals.network.dto.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // A futuro, aquí añadiremos más funciones como register, getTournaments, etc.
    @GET("auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<User> // Esperamos recibir el objeto User que acabamos de crear

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<User> // Esperamos que el servidor nos devuelva el usuario creado

    @POST("tournaments")
    suspend fun createTournament(
        @Header("Authorization") token: String,
        @Body request: CreateTournamentRequest
    ): Response<Tournament> // Esperamos que devuelva el torneo recién creado
}