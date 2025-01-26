/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.model

data class CursoResponse(
    val id: Int,
    val codCurso: String,
    val titulo: String,
    val etapa: String,
    val nivel: String,
    val activo: Int
)