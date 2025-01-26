/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.ProfesorResponse

data class ProfesorResponsableResponse(
    val id: Int,
    val actividad: ActividadResponse,
    val profesor: ProfesorResponse
)