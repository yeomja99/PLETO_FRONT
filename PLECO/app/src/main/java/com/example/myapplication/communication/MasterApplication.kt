package com.example.myapplication.communication

import android.app.Application
import android.content.Context
import com.example.myapplication.interfaces.RetrofitService
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication : Application() {

    lateinit var service: RetrofitService

    override fun onCreate() {
        super.onCreate()

        createRetrofit() // 사용자 로그인 여부에 의해 retrofit을 만들어냄 (토큰 있, 없)
    }

    fun createRetrofit() {
        val header = Interceptor {
            val original = it.request()

            if (checkIsLogin()) {
                getUserToken()?.let { token ->  // null이 아닌 경우 let 블럭 실행
                    val request = original.newBuilder() // 원래 나가려던 통신을 잡아서 original에 헤더를 붙임
                        .header("Authorization", "$token")
                        // name : 서버가 설정한거 보고 다시 작성, value : 서버가 토큰 설정한거 보고 작성
                        .build()
                    it.proceed(request) // 헤더 붙이고 내보냄
                }
            } else {
                it.proceed(original)
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.46.179:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    // 로그인 확인
    // sharedPreference에 토큰 값이 있으면 로그인 된 것으로 간주, 없으면 로그인 안된 것으로 간주
    fun checkIsLogin(): Boolean {
        val sp = getSharedPreferences("login_token", Context.MODE_PRIVATE) // sp에서 값을 가져옴
        val token = sp.getString("login_token", "null")
        return token != "null"  // 토큰이 null이 아니면 true, null이면 false 반환
    }

    // 토큰 값을 내보냄
    // 로그인이 안된 경우 null을 내보내야 하므로 nullable
    fun getUserToken(): String? {
        val sp = getSharedPreferences("login_token", Context.MODE_PRIVATE)
        val token = sp.getString("login_token", "null")
        return if (token == "null") null    // 토큰이 null이면 null
        else token  // null이 아니면 토큰 값 내보냄
    }

}