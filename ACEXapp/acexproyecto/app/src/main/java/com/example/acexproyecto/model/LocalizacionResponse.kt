package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse

data class LocalizacionResponse(
    val id: Int,
    val idActividad: ActividadResponse,
    val latitud: Double,
    val longitud: Double,
    val comentario: String
)
