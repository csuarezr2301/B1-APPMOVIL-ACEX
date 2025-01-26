/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.appacex.model

import com.example.acexproyecto.model.Departamento

data class ProfesorResponse(
    val uuid: String,
    val dni: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val rol: String,
    val activo: Int,
    val urlFoto: String?,
    val esJefeDep: Int,
    val depart: Departamento
)



