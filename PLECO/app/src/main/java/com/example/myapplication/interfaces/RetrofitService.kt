package com.example.myapplication.interfaces

import com.example.myapplication.communication.Email
import com.example.myapplication.communication.SignUpOkCheck
import com.example.myapplication.communication.UserInfo
import com.example.myapplication.communication.UserToken
import com.example.myapplication.utils.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    // 밑에 주석은 예시임. 참고만 할 것
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
    @POST("signup") // base url 뒤에 오는 url을 적을 것
    fun register(
            @Body userInfo: UserInfo   // email, password
    ): Call<SignUpOkCheck>

    // 서버 완성되면 수정할 것
    @Headers("content-type: application/json")
    @GET("duplicate")
    fun getNicknameIsExist(
            @Query("email") email: String // query는 ? 뒤에 오는 것
    ): Call<Email>

    // 로그인 POST
//        @Headers("content-type: application/json")
//        @POST("login")
//        fun login(
//            @Body params: HashMap<String, String>       // 회원가입처럼 userInfo 로 보내도 상관없음
//        ): Call<UserToken>

    @Headers("content-type: application/json")
    @POST("login")
    fun login(
            @Body params: HashMap<String, String>       // 회원가입처럼 userInfo 로 보내도 상관없음
//                @Body userInfo: UserInfo   // email, password
    ): Call<UserToken>

    // GrowUpPlee 관련 함수
    //유저가 현재 키우고 있는 플리와 eco 수행 횟수 가져오기
    @Headers("content-type: application/json", "X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MDkwNiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzA5OTEzMDAsImV4cCI6MTYzMDk5NDkwMH0.rR8yizDa1sne_DyO52z22rmBkr6NAm69-HyVflw6EMw")
    @GET("user/growPlee")
    fun GetGrowPlee(
            @Query("email") email: String
    ): Call<GrowPleeData>

    //생성한 플리 보내기
    @Headers("content-type: application/json", "X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MDkwNiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzA5OTEzMDAsImV4cCI6MTYzMDk5NDkwMH0.rR8yizDa1sne_DyO52z22rmBkr6NAm69-HyVflw6EMw")
    @POST("user/growPlee")
    fun PostNowPlee(
            @Body pleeStateData: PleeStateData
    ): Call<Long>

    //있는 플리 리스트 가져오기
    @Headers("content-type: application/json", "X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MDkwNiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzA5OTEzMDAsImV4cCI6MTYzMDk5NDkwMH0.rR8yizDa1sne_DyO52z22rmBkr6NAm69-HyVflw6EMw")
    @GET("user/pleeDict")
    fun GetPleelist(
            @Query("email") email: String
    ): Call<PleeDictData>

    // 현재 상태 가져오기
    @Headers("content-type: application/json", "X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MDkwNiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MzA5OTEzMDAsImV4cCI6MTYzMDk5NDkwMH0.rR8yizDa1sne_DyO52z22rmBkr6NAm69-HyVflw6EMw")
    @POST("user/performEco")
    fun CheckStatus(
            @Body sendPleeStatus: SendPleeStatus
    ): Call<PleeStatus>

}