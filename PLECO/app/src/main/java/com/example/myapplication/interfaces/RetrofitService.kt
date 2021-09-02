package com.example.myapplication.interfaces

import com.example.myapplication.communication.Email
import com.example.myapplication.communication.User
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
        // 회원가입 POST
//    @POST("users")
//    @FormUrlEncoded // Field를 하나하나 보낼 때 적어줘야 함
//    fun register(
//        // @Body register: Register // Body에 Register라는 객체를 넣어 api 요청
//        @Field("nickname") nickname: String,
//        @Field("password") password: String
//    ): Call<User>   // 응답으로 User 객체가 반환

        // 회원가입 POST
        @Headers("content-type: application/json")
        @POST("signup")
        fun register(
            @Body user: User   // nickname, password
        ): Call<User>

        // 닉네임 중복 확인 GET
        // 서버 완성되면 수정할 것
        @Headers("content-type: application/json")
        @GET("signup")
        fun getNicknameIsExist(
            @Query("nickname") nickname: String
        ): Call<Email>

        // 로그인 POST
        @Headers("content-type: application/json")
        @POST("login")
        fun login(
            @Body params: HashMap<String, String>
        ): Call<User>
}