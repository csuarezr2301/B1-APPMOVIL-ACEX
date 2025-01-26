package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.ProfesorResponse

data class PhotoResponse (
    val id: Int,
    val urlFoto: String?,
    var descripcion: String,
    val actividad: ActividadResponse
)