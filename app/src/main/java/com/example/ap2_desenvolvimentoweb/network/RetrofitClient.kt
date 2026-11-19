package com.example.ap2_desenvolvimentoweb.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 é o "localhost" do computador visto pelo emulador Android.
    // Em celular físico, troque pelo IP do computador na rede (ex.: http://192.168.0.10:8000/).
    const val BASE_URL = "http://10.0.2.2:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}