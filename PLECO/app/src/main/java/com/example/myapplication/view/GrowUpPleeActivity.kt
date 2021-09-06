package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.communication.Email
import com.example.myapplication.communication.MasterApplication
import com.example.myapplication.communication.SignUpOkCheck
import com.example.myapplication.utils.GrowPleeData
import com.example.myapplication.utils.PleeDictData
import kotlinx.android.synthetic.main.activity_grow_up_plee.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.ArrayList


val missionnum1: Int = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Int = 5// 2단계 미션 횟수 저장 변수

class GrowUpPleeActivity : AppCompatActivity() {
    private var PleeListSize = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)

        var existedPleeList : PleeDictData = GetPleeList()
        // 유저가 생성한 Plee가 없다면
        if (PleeListSize== 0){
            var tutorialImageview : ImageView = findViewById(R.id.view_plee)
            tutorialImageview.setImageResource(R.drawable.example_ducky)
            // 튜토리얼 플리 생성
            // 플리 저장 리스트 가져오고
            // 가져온 플리 중에 T(tutorial)로 분류된 플리 이름, 사진 불러오기
            // 불러온 플리를 layout에 띄워주기
            // 튜토리얼 플리 정보 보내주기(플리 이름, 미션 완료 횟수)
        }

        // 유저가 튜토리얼 plee만 생성했다면
        else if (existedPleeList.pleeList!!.size >= 1){
            // Complete 일 때
            // 플리 저장 리스트 가져오고
            // 가져온 플리 중에 R(real) 로 분류된 플리 이름, 사진 불러오기
            // 불러온 플리를 layout에 띄워주기
            // 실제 플리 정보 보내주기(플리 이름, 미션 완료 횟수)

            // Growing 일 때
            // 기존 정보 유지
        }

        // plee를 다 모았을 경우
        // 당신은 진정한 지구지킴이! 텍스트 띄우기

        Growup_EcoGallery.setOnClickListener {
            var growup2ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(growup2ecogallery_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)
        }
        Growup_Camera.setOnClickListener {
            var growup2camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(growup2camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)
        }
        Growup.setOnClickListener {
            var growup_intent: Intent = Intent(this, GrowUpPleeActivity::class.java)
            startActivity(growup_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)
        }
        Growup_Userinfo.setOnClickListener {
            var growup2userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(growup2userinfo_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    //     1) 현재 Plee 조회

    //    1-1) 성장률 가시화 함수
    //    progressbar에 진행률 넘겨주기 참고 사이트: https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=kimsh2244&logNo=221069589979
    //    이메일은 로그인 담당하는 사람이 변수에 저장해 놓아야 함
    private fun ShowGrowingRate(ecoedNum: Int) {  // 입력: 진행한 미션 횟수
        var ecoedState: Array<Int> = CheckState(ecoedNum)// 진행한 미션 단계(seed, middle, plant)
        var GrowingRate: Int = 0

        // 0 --> 1 단계
        if (ecoedState[0] == 0) {
            if (ecoedState[1] == 0) {
                GrowingRate = 0
            } else if (ecoedState[1] > 0) {
                GrowingRate = ecoedState[1] / missionnum1 * 100
            }
        }
        // 1 --> 2 단계
        else if (ecoedState[0] == 1) {
            if (ecoedState[1] == 0) {
                GrowingRate = 0
            } else if (ecoedState[1] > 0) {
                GrowingRate = ecoedState[1] / missionnum2 * 100
            }
        }

        var progressbar: ProgressBar = state_bar
        progressbar.setProgress(GrowingRate)
    }

    // 현재 Plee 단계 체크 함수
    private fun CheckState(ecoedNum: Int): Array<Int> {
        var ecoedState: Array<Int> = arrayOf(0, 0) // 첫번째 원소: 단계, 두번째 원소: 다음 단계까지 남은 미션 수
        if (ecoedNum < missionnum1) {
            ecoedState[0] = 0
            ecoedState[1] = ecoedNum
        } else if (ecoedNum >= missionnum1 && ecoedNum < missionnum1 + missionnum2) {
            ecoedState[0] = 1
            ecoedState[1] = ecoedNum - missionnum1
        } else {
            ecoedState[0] = 2
            ecoedState[1] = 0
        }
        return ecoedState
    }

    //    1-2) 현재 Plee data get() 함수
    private fun GetGrowingPleeData(): GrowPleeData {
        lateinit var growingPlee: GrowPleeData
        (application as MasterApplication).service.GetGrowPlee(growingPlee)
            .enqueue(object :
                Callback<GrowPleeData> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<GrowPleeData>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<GrowPleeData>,
                    response: Response<GrowPleeData>
                ) {   // 통신 성공
                    val result = response.body()
                    if (response.isSuccessful) {
                        val result = response.body()
                        growingPlee.ecoCount = result?.ecoCount
                        growingPlee.pleeName = result?.pleeName
                    }
                }
            })
        return growingPlee

    }

    private fun GetPleeList() : PleeDictData{
        lateinit var exsitedPleeList: PleeDictData
        (application as MasterApplication).service.GetPleelist(exsitedPleeList)
            .enqueue(object :
                Callback<PleeDictData> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeDictData>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("통신 오류","fail")
                }

                override fun onResponse(
                    call: Call<PleeDictData>,
                    response: Response<PleeDictData>
                ) {   // 통신 성공
                    val result = response.body()
                    if (response.isSuccessful) {
                        val result = response.body()
                        exsitedPleeList = result!!
                    }
                }
            })
        return exsitedPleeList
    }


    //     2) Plee 초기화(Plee 생성)
    //    2-1) 튜토리얼 함수
    //        - 미션 1회 진행시 성장(seed -> plant, 중간 단계 제거)
    private fun PleeTutorial() {

    }

    //    2-2) Plee 생성 함수
    //        - Plee가 존재하지 않을 때: random으로 Plee 생성
    //        - 성장이 완료된 Plee가 존재할 때: 성장이 완료된 Plee 제외한 다른 Plee random으로 생성
    //        - Plee 만든 마지막 상태 보내주기(아직 미션중이야, 미션 다완료해서 다 컸어)
    fun CreatePlee() {
        //        1. complete를 받으면 생성하기(string)
    }
}