package com.example.appacex.model

import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import com.example.acexproyecto.model.PhotoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @Multipart
    @POST("api/foto/upload")
    fun uploadPhotos(
        @Part fotos: List<MultipartBody.Part>,
        @Query("idActividad") idActividad: Int,
        @Query("descripcion") descripcion: String
    ): Call<Void>

    @GET("api/foto")
    fun getFotos(): Call<List<PhotoResponse>>

    @DELETE("api/foto/{id}")
    suspend fun deleteFoto(@Path("id") id: Int): Response<Void>

}

