package com.example.ap2_desenvolvimentoweb.model

import com.google.gson.annotations.SerializedName

data class RecomendacaoRequest(
    val nome: String,
    val humor: String,
    val generos: List<String>,
    @SerializedName("aceita_classicos") val aceitaClassicos: Boolean,
    @SerializedName("duracao_max") val duracaoMax: Int
)

data class AtualizacaoRequest(
    val nota: Int?
)

data class Recomendacao(
    val id: Int,
    val nome: String,
    val humor: String,
    val generos: List<String>,
    @SerializedName("aceita_classicos") val aceitaClassicos: Boolean,
    @SerializedName("duracao_max") val duracaoMax: Int,
    @SerializedName("filme_titulo") val filmeTitulo: String,
    @SerializedName("filme_ano") val filmeAno: Int,
    @SerializedName("filme_genero") val filmeGenero: String,
    @SerializedName("filme_duracao") val filmeDuracao: Int,
    @SerializedName("filme_sinopse") val filmeSinopse: String,
    val compatibilidade: Int,
    val nota: Int?,
    @SerializedName("criado_em") val criadoEm: String?
)