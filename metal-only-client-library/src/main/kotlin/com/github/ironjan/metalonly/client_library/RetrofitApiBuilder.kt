package com.github.ironjan.metalonly.client_library

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitApiBuilder {
    val METAL_ONLY_API_BASE_URL = "https://www.metal-only.de/botcon/"

    private val builder: Retrofit = Retrofit.Builder()
            .baseUrl(METAL_ONLY_API_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

    val api: ApiRetrofitRewrite = builder.create(ApiRetrofitRewrite::class.java)

}
