package com.example.appacex.model

data class ActividadResponse(
    val id: Int,
    var titulo: String,
    val tipo: String,
    var descripcion: String,
    val fini: String,
    val ffin: String,
    val hini: String,
    val hfin: String,
    val previstaIni: Int,
    val transporteReq: Int,
    val comentTransporte: String?,
    val alojamientoReq: Int,
    val comentAlojamiento: String?,
    val comentarios: String,
    val estado: String,
    val comentEstado: String,
    val incidencias: String,
    val urlFolleto: String?,
    val solicitante: ProfesorResponse,
    val importePorAlumno: Double,
    var latitud: Double,
    var longitud: Double

)
