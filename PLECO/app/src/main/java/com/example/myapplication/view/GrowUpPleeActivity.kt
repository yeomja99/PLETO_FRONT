package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_grow_up_plee.*


val tutorialnum: Int = 1// tutorial 미션 횟수 저장 변수
val missionnum1: Int = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Int = 5// 2단계 미션 횟수 저장 변수

class GrowUpPleeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)


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

        btn_move2pleelist.setOnClickListener {
            var move2pleelist_intent = Intent(this, PleeListActivity::class.java)
            startActivity(move2pleelist_intent)
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
        if (ecoedState[0] == 0){
            if (ecoedState[1] == 0){
                GrowingRate = 0
            }
            else if (ecoedState[1] > 0){
                GrowingRate = ecoedState[1] / missionnum1 * 100
            }
        }
        // 1 --> 2 단계
        else if (ecoedState[0] == 1){
            if (ecoedState[1] == 0){
                GrowingRate = 0
            }
            else if (ecoedState[1] > 0){
                GrowingRate = ecoedState[1] / missionnum2 * 100
            }
        }

        var progressbar: ProgressBar = state_bar
        progressbar.setProgress(GrowingRate)
    }

    // 현재 Plee 단계 체크 함수
    private fun CheckState(ecoedNum: Int): Array<Int> {
        var ecoedState : Array<Int> = arrayOf(0, 0) // 첫번째 원소: 단계, 두번째 원소: 다음 단계까지 남은 미션 수
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
    private fun PleeDataGet() {

    }

    //     2) Plee 초기화(Plee 생성)
//    2-1) 튜토리얼 함수
//        - 미션 1회 진행시 성장(seed -> plant, 중간 단계 제거)
    private fun PleeTutorial() {

    }

    //    2-2) Plee 생성 함수
//        - Plee가 존재하지 않을 때: random으로 Plee 생성
//        - 성장이 완료된 Plee가 존재할 때: 성장이 완료된 Plee 제외한 다른 Plee random으로 생성
    fun CreatePlee() {

    }
}