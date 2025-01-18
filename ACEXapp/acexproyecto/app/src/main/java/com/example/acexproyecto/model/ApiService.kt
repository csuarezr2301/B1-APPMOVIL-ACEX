package com.example.appacex.model

import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import org.simpleframework.xml.Path
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/profesor")
    fun getProfesores(): Call<List<ProfesorResponse>>

    @GET("api/actividad")
    fun getActividades(): Call<List<ActividadResponse>>

    @GET("api/grupoParticipante")
    fun getGrupoParticipantes(): Call<List<GrupoParticipanteResponse>>

    @GET("api/profParticipante")
    fun getProfesoresparticipantes(): Call<List<ProfesorParticipanteResponse>>

    @GET("api/grupo")
    fun getGrupos(): Call<List<GrupoResponse>>
}

