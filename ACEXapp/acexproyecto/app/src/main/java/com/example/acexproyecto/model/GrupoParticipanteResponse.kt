/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse

data class GrupoParticipanteResponse(
    val id: Int,
    val actividades: ActividadResponse,
    val grupo: GrupoResponse,
    var numParticipantes: Int,
    val comentario: String?
)