package com.example.ap2_desenvolvimentoweb.network

import com.example.ap2_desenvolvimentoweb.model.AtualizacaoRequest
import com.example.ap2_desenvolvimentoweb.model.Recomendacao
import com.example.ap2_desenvolvimentoweb.model.RecomendacaoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("recomendacoes")
    fun criar(@Body body: RecomendacaoRequest): Call<Recomendacao>

    @GET("recomendacoes")
    fun listar(): Call<List<Recomendacao>>

    @GET("recomendacoes/{id}")
    fun consultar(@Path("id") id: Int): Call<Recomendacao>

    @PUT("recomendacoes/{id}")
    fun atualizar(@Path("id") id: Int, @Body body: AtualizacaoRequest): Call<Recomendacao>

    @DELETE("recomendacoes/{id}")
    fun excluir(@Path("id") id: Int): Call<Void>
}