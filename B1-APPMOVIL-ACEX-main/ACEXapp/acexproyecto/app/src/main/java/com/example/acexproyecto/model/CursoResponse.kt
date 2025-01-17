package com.example.acexproyecto.model

data class CursoResponse(
    val id: Int,
    val codCurso: String,
    val titulo: String,
    val etapa: String,
    val nivel: String,
    val activo: Int
)