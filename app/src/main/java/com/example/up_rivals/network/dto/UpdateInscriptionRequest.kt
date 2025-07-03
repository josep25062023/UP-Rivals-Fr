// En: network/dto/UpdateInscriptionRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateInscriptionRequest(
    @SerializedName("status")
    val status: String // Enviaremos "approved" o "rejected"
)