package com.example.acexproyecto.model

import com.example.appacex.model.ActividadResponse

data class GrupoParticipanteResponse(
    val id: Int,
    val actividades: ActividadResponse,
    val grupo: GrupoResponse,
    var numParticipantes: Int,
    val comentario: String?
)