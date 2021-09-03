package com.example.myapplication.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GrowUpPleeRetrofitService {

    //유저가 현재 키우고 있는 플리와 eco 수행 횟수 가져오기
    @GET("growPlee")
    fun GetGrowPlee(
        @Body growPleeData: GrowPleeData
    ): Call<GrowPleeData>

    //생성한 플리 보내기
    @POST("growPlee")
    fun PostNowPlee(
        @Body pleeStateData: PleeStateData
    ): Call<PleeStateData>

    //있는 플리 리스트 가져오기
    @GET("pleeDict")
    fun GetPleelist(
        @Body pleeDictData: PleeDictData
    ): Call<PleeDictData>


}