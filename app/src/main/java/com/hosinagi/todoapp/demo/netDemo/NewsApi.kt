package com.hosinagi.todoapp.demo.netDemo


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface NewsApi {

    @GET("news/list")
    fun getNews() : Call<NewsBean>

    @FormUrlEncoded
    @POST("user/login3")
    fun loginFrom(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Any>

    @POST("user/login4")
    fun loginJson(
        @Body user: User
    ): Call<Any>
}

data class User(
    val username: String,
    val password: String
)