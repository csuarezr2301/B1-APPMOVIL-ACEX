/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse

data class PhotoResponse (
    val id: Int,
    val urlFoto: String?,
    var descripcion: String,
    val actividad: ActividadResponse
)