package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.communication.*
import com.example.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_grow_up_plee.*
import okhttp3.ResponseBody
import retrofit2.*
import java.io.IOException
import java.lang.StrictMath.random
import java.util.*

val tutorialnum: Int = 1// 튜토리얼 미션 횟수 저장 변수
val missionnum1: Int = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Int = 5// 2단계 미션 횟수 저장 변수


class GrowUpPleeActivity : AppCompatActivity() {
    private var allPleeList: Array<String> = arrayOf("AS", "aS", "BD")

    private var PleeListSize = 0
    private var status = PleeStatus() // complete/growing 저장
    private var existedPleeList = PleeDictData() // 유저가 가지고 있는 플리 저장
    private var growingplee = GrowPleeData() // 현재 성장하고 있는 플리 이름과 수행한 미션 횟수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)

        val sp = getSharedPreferences("login_token", Context.MODE_PRIVATE)
        val token = sp.getString("login_token", "null")
        Log.d("SavedUserTokenInGrowUp", token)

        var ex_plee = SendPleeStatus()
        growingplee.ecoCount = 1
        growingplee.pleeName = "test"
        ex_plee.ecoName = "텀블러"
        ex_plee.email = "test0907"
        ex_plee.pleeName = "cute"
        status = checkPleeStatus(ex_plee) //complet/growing 상태 저장
        existedPleeList = GetPleeList("test0907")
        growingplee = GetGrowingPleeData("test0907")
        status.pleeStatus = "COMPLETE"

        if (status.pleeStatus == "COMPLETE") {
            if (PleeListSize == 0) { // 플리가 하나도 없을 경우 튜토리얼 캐릭터 생성
                Toast.makeText(this@GrowUpPleeActivity, "튜토리얼 플리를 성장시켜주세요!", Toast.LENGTH_LONG)
                    .show()
                val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                val tutorialTextView: TextView = findViewById(R.id.view_pleename)
                var pleeid: Int = getResources("drawable/", "ic_btn_rename")
                tutorialImageView.setImageResource(pleeid)
                tutorialTextView.setText("튜토리얼 플리")

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)

                var postdata = PleeStateData("튜토링러 플리", 1)
                PostPlee(postdata)

            } else if (existedPleeList.pleeList!!.size >= 1) {
                Toast.makeText(this@GrowUpPleeActivity, "새로운 플리를 만나볼까요?", Toast.LENGTH_LONG)
                    .show()
                val PleeImageView: ImageView = findViewById(R.id.view_plee)
                val PleeTextView: TextView = findViewById(R.id.view_pleename)
                val newPleeList = allPleeList.distinctBy { existedPleeList.pleeList }
                val newPlee = newPleeList.random() // 새로운 플리 이름
                var pleeid: Int = getResources("drawable", newPlee)
                PleeImageView.setImageResource(pleeid)
                PleeTextView.setText(newPlee)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)

                var postdata = PleeStateData(newPlee, missionnum1 + missionnum2)
                PostPlee(postdata)

            }
        } else { // growing
            if (growingplee.ecoCount!! < missionnum1) { // 0 --> 1단계 수행중
                Toast.makeText(this@GrowUpPleeActivity, "성장할고얏!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("1단계까지")
                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(growingplee.pleeName)

                var progressbar: ProgressBar = state_bar
                var GrowingRate = (growingplee.ecoCount!! / missionnum1) * 100
                progressbar.setProgress(GrowingRate.toInt())

            }
            // 2단계 이미지 바로 보여주면서 성장률 0퍼센트부터 시작
            else if (growingplee.ecoCount!! == missionnum1.toLong()) { // 1단계 도착
                Toast.makeText(this@GrowUpPleeActivity, "1단계 도착!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("2단계까지")
                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(growingplee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)
            } else if (growingplee.ecoCount!! > missionnum1.toLong() && growingplee.ecoCount!! < missionnum1.toLong() + missionnum2.toLong()) { // 1 --> 2단계

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료")
                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(growingplee.pleeName)

                var progressbar: ProgressBar = state_bar
                var GrowingRate = (growingplee.ecoCount!! / missionnum1) * 100
                progressbar.setProgress(GrowingRate.toInt())
            } else if (growingplee.ecoCount!! == missionnum1.toLong() + missionnum2.toLong()) { // 2단계까지 성장 완료

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료")
                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(growingplee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(100)
            }
        }



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
    private fun GetGrowingPleeData(email: String): GrowPleeData {
        var growingPlee = GrowPleeData()
        (application as MasterApplication).service.GetGrowPlee(Token.token, email)
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
                    Log.d("response token", Token.token)
                    Log.d("response code!!!", " " + response.code())
                    Log.d("response body", response.body().toString())
                    if (response.isSuccessful) {
                        val result = response.body()
                        growingPlee.ecoCount = result?.ecoCount
                        growingPlee.pleeName = result?.pleeName
                    }
                    else{
                        val converter: Converter<ResponseBody, LogInErrorMessage> =
                            (application as MasterApplication).retrofit.responseBodyConverter(
                                LogInErrorMessage::class.java, arrayOfNulls<Annotation>(0)
                            )

                        val error: LogInErrorMessage

                        try {
                            error = converter.convert(response.errorBody())!!
                            Log.e("error message", error.getErrorMessage())
                            Toast.makeText(this@GrowUpPleeActivity, error.getErrorMessage(), Toast.LENGTH_LONG).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        return growingPlee

    }

    // 존재하는 플리 리스트 get 함수
    private fun GetPleeList(email: String): PleeDictData {
        Log.d("existedPleeList", "함수 시작")
        var exsitedPleeList = PleeDictData()
        (application as MasterApplication).service.GetPleelist(email)
            .enqueue(object :
                Callback<PleeDictData> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeDictData>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("통신 오류", "fail")
                }

                override fun onResponse(
                    call: Call<PleeDictData>,
                    response: Response<PleeDictData>
                ) {   // 통신 성공
                    val result = response.body()
                    Log.d("existedPleeList", " " + response.code())
                    if (response.isSuccessful) {
                        exsitedPleeList = result!!
                    }
                }
            })
        return exsitedPleeList
    }

    // 생성한 플리 전달 서버에 post
    private fun PostPlee(pleestatedata: PleeStateData) {
        (application as MasterApplication).service.PostNowPlee("test0907", pleestatedata)
            .enqueue(object :
                Callback<Long> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<Long>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("통신 오류", "fail")
                }

                override fun onResponse(
                    call: Call<Long>,
                    response: Response<Long>
                ) {   // 통신 성공
                    val result = response.body()
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("PostPlee", "" + response.code())
                    }
                }
            })
    }

    // plee status 체크 함수
    private fun checkPleeStatus(sendpleestatus: SendPleeStatus): PleeStatus {
        var status = PleeStatus()
        Log.d("checkPleeStatus", "1단계 통과:" + sendpleestatus.ecoName)
        (application as MasterApplication).service.CheckStatus(sendpleestatus)
            .enqueue(object :
                Callback<PleeStatus> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeStatus>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("통신 오류", "fail")
                }

                override fun onResponse(
                    call: Call<PleeStatus>,
                    response: Response<PleeStatus>
                ) {   // 통신 성공
                    val result = response.body()
                    Log.d("checkPleeStatus", "2단계 통과:" + response.body())
                    Log.d("checkPleeStatus", "코드:" + response.code())
                    if (response.isSuccessful) {
                        status = result!!
                        Log.d("통신 성공", "" + status)
                    }
                }
            })
        return status
    }

    private fun getResources(type: String, name: String): Int {
        return super.getResources().getIdentifier(type + name, null, packageName)
    }


}