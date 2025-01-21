package com.example.appacex.model

import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/profesor")
    fun getProfesores(): Call<List<ProfesorResponse>>

    @GET("api/actividad")
    fun getActividades(): Call<List<ActividadResponse>>

    @PUT("api/actividad/{id}")
    suspend fun updateActividad(@Path("id") id: Int, @Body actividad: ActividadResponse): Response<Void>

    @GET("api/grupoParticipante")
    fun getGrupoParticipantes(): Call<List<GrupoParticipanteResponse>>

    @POST("api/grupoParticipante")
    suspend fun addGrupoParticipante(@Body grupoParticipante: GrupoParticipanteResponse): Response<Void>

    @DELETE("api/grupoParticipante/{id}")
    suspend fun deleteGrupoParticipante(@Path("id") id: Int): Response<Void>

    @GET("api/profParticipante")
    fun getProfesoresparticipantes(): Call<List<ProfesorParticipanteResponse>>

    @POST("api/profParticipante")
    suspend fun addProfesorParticipante(@Body profesorParticipante: ProfesorParticipanteResponse): Response<Void>

    @DELETE("api/profParticipante/{id}")
    suspend fun deleteProfesorParticipante(@Path("id") id: Int): Response<Void>

    @GET("api/grupo")
    fun getGrupos(): Call<List<GrupoResponse>>
}

