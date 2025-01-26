/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.objetos

import com.example.appacex.model.ProfesorResponse

object Usuario {
    var displayName: String = ""
    var photoPath: String = ""
    var account: String = ""
    var apiToken: String = ""
    var msalToken: String = ""
    var calendarId: String = ""
    var profesor: ProfesorResponse? = null
}