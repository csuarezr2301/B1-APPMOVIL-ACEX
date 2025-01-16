package com.example.acexproyecto.model

import com.example.appacex.model.ProfesorResponse

data class GrupoResponse(
    val id: Int,
    val curso: CursoResponse,
    val codGrupo: String,
    val numAlumnos: Int,
    val activo: Int,
    val tutor: ProfesorResponse
)
