/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

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
