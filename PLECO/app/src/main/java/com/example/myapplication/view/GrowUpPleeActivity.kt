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

val tutorialnum: Long = 1// 튜토리얼 미션 횟수 저장 변수
val missionnum1: Long = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Long = 5// 2단계 미션 횟수 저장 변수
var isexsited: Boolean = false// 유저가 플리가 있는지 없는지 확인하는 함수


class GrowUpPleeActivity : AppCompatActivity() {
    private var allPleeList: Array<String> = arrayOf("AS", "aS", "BD")  // 예시 플리리스트 추후 수정 필요!!!

    private var PleeListSize = 0
    private var status = PleeStatus() // complete/growing 저장
    private var existedPleeList = PleeDictData() // 유저가 가지고 있는 플리 저장
    private var growingplee = GrowPleeData() // 현재 성장하고 있는 플리 이름과 수행한 미션 횟수
    private var ex_email = "test0909"   // 예시 이메일
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)

        var ex_plee = SendPleeStatus() // Plee 상태 보내면 Complete/Growing plee status 받음

        // 초기화 하지 않으면 에러나서 추가한 코드
//        PostPlee(PleeStateData("one",1))

//        Log.d("isexsited", "" + isexsited)
//        existedPleeList = GetPleeList(ex_email) // 유저에게 존재하는 플리 리스트 Get
        growingplee = GetGrowingPleeData(ex_email)  // 자라는 플리 정보(플리 이름, 미션 진행 횟수) Get

        ex_plee.ecoName = "tumbler"       // 에코 미션
        ex_plee.email = ex_email      // 예시 이메일 정보
        status = checkPleeStatus(ex_plee) //complete/growing 상태 저장

//        Log.d("status", "status: " + status.pleeStatus)
//        Log.d("GrowingPlee", "pleename: " + growingplee.ecoCount)

//        if (status.pleeStatus == "COMPLETE") {
//            if (PleeListSize == 0) { // 플리가 하나도 없을 경우 튜토리얼 캐릭터 생성
//                Toast.makeText(this@GrowUpPleeActivity, "튜토리얼 플리를 성장시켜주세요!", Toast.LENGTH_LONG)
//                    .show()
//                val tutorialImageView: ImageView = findViewById(R.id.view_plee)
//                val tutorialTextView: TextView = findViewById(R.id.view_pleename)
//                var pleeid: Int = getResources("drawable/", "example_ducky")
//                tutorialImageView.setImageResource(pleeid)
//                tutorialTextView.setText("튜토리얼 플리")
//
//                var progressbar: ProgressBar = state_bar
//                progressbar.setProgress(0)
//
//                var postdata = PleeStateData()
//                postdata.pleeName = "nnplee7"
//                postdata.completeCount = 10 // 추후 수정
//                PostPlee(postdata)
//
//            } else if (existedPleeList.pleeList!!.size >= 1) {
//                Toast.makeText(this@GrowUpPleeActivity, "새로운 플리를 만나볼까요?", Toast.LENGTH_LONG)
//                    .show()
//                val PleeImageView: ImageView = findViewById(R.id.view_plee)
//                val PleeTextView: TextView = findViewById(R.id.view_pleename)
//                val newPleeList = allPleeList.distinctBy { existedPleeList.pleeList }
//                val newPlee = newPleeList.random() // 새로운 플리 이름
//                var pleeid: Int = getResources("drawable", newPlee)
//                PleeImageView.setImageResource(pleeid)
//                PleeTextView.setText(newPlee)
//
//                var progressbar: ProgressBar = state_bar
//                progressbar.setProgress(0)
//
//                var postdata = PleeStateData(newPlee, missionnum1 + missionnum2)
//                PostPlee(postdata)
//
//            }
//        } else { // growing
//            if (growingplee.ecoCount!! < missionnum1) { // 0 --> 1단계 수행중
//                Toast.makeText(this@GrowUpPleeActivity, "성장할고얏!", Toast.LENGTH_LONG)
//                    .show()
//                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
//                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
//                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
//                nextStateTextView.setText("1단계까지")
//                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
//                GrowPleeImageView.setImageResource(pleeid)
//                GrowPleeTextView.setText(growingplee.pleeName)
//
//                var progressbar: ProgressBar = state_bar
//                var GrowingRate = (growingplee.ecoCount!! / missionnum1) * 100
//                progressbar.setProgress(GrowingRate.toInt())
//
//            }
//            // 2단계 이미지 바로 보여주면서 성장률 0퍼센트부터 시작
//            else if (growingplee.ecoCount!! == missionnum1.toLong()) { // 1단계 도착
//                Toast.makeText(this@GrowUpPleeActivity, "1단계 도착!", Toast.LENGTH_LONG)
//                    .show()
//                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
//                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
//                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
//                nextStateTextView.setText("2단계까지")
//                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
//                GrowPleeImageView.setImageResource(pleeid)
//                GrowPleeTextView.setText(growingplee.pleeName)
//
//                var progressbar: ProgressBar = state_bar
//                progressbar.setProgress(0)
//            } else if (growingplee.ecoCount!! > missionnum1.toLong() && growingplee.ecoCount!! < missionnum1.toLong() + missionnum2.toLong()) { // 1 --> 2단계
//
//                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
//                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
//                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
//                nextStateTextView.setText("성장 완료")
//                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
//                GrowPleeImageView.setImageResource(pleeid)
//                GrowPleeTextView.setText(growingplee.pleeName)
//
//                var progressbar: ProgressBar = state_bar
//                var GrowingRate = (growingplee.ecoCount!! / missionnum1) * 100
//                progressbar.setProgress(GrowingRate.toInt())
//            } else if (growingplee.ecoCount!! == missionnum1.toLong() + missionnum2.toLong()) { // 2단계까지 성장 완료
//
//                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
//                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
//                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
//                nextStateTextView.setText("성장 완료")
//                var pleeid: Int = getResources("drawable", growingplee.pleeName!!)
//                GrowPleeImageView.setImageResource(pleeid)
//                GrowPleeTextView.setText(growingplee.pleeName)
//
//                var progressbar: ProgressBar = state_bar
//                progressbar.setProgress(100)
//            }
//        }


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
    private fun ShowGrowingRate(ecoedNum: Long) {  // 입력: 진행한 미션 횟수
        var ecoedState: Array<Long> = CheckState(ecoedNum)// 진행한 미션 단계(seed, middle, plant)
        var GrowingRate: Long = 0

        // 0 --> 1 단계
        if (ecoedState[0] == 0.toLong()) {
            if (ecoedState[1] == 0.toLong()) {
                GrowingRate = 0
            } else if (ecoedState[1] > 0) {
                GrowingRate = ecoedState[1] / missionnum1 * 100
            }
        }
        // 1 --> 2 단계
        else if (ecoedState[0] == 1.toLong()) {
            if (ecoedState[1] == 0.toLong()) {
                GrowingRate = 0
            } else if (ecoedState[1] > 0) {
                GrowingRate = ecoedState[1] / missionnum2 * 100
            }
        }

        var progressbar: ProgressBar = state_bar
        progressbar.setProgress(GrowingRate.toInt())
    }

    // 현재 Plee 단계 체크 함수
    private fun CheckState(ecoedNum: Long): Array<Long> {
        var ecoedState: Array<Long> = arrayOf(0, 0) // 첫번째 원소: 단계, 두번째 원소: 다음 단계까지 남은 미션 수
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


    // 현재 플리 이름과 미션 진행한 횟수 Get 함수
    // 현재 (pleeName, ecoCount) Get
    private fun GetGrowingPleeData(email: String): GrowPleeData {
        var growingPlee = GrowPleeData()
        (application as MasterApplication).service.GetGrowPlee(Token.token, email)
            .enqueue(object :
                Callback<GrowPleeData> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<GrowPleeData>,
                    t: Throwable
                ) {    // 통신 실패
                    Log.d("GetGrowingPleeData", "통신 오류")
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
                    } else {
                        val converter: Converter<ResponseBody, LogInErrorMessage> =
                            (application as MasterApplication).retrofit.responseBodyConverter(
                                LogInErrorMessage::class.java, arrayOfNulls<Annotation>(0)
                            )

                        val error: LogInErrorMessage

                        try {
                            error = converter.convert(response.errorBody())!!
                            Log.e("error message", error.getErrorMessage())
                            Toast.makeText(
                                this@GrowUpPleeActivity,
                                error.getErrorMessage(),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        return growingPlee

    }

    // 유저가 가지고 있는 플리 리스트 GET 함수
    // pleename: Stirng으로 구성된 Dict Get
    private fun GetPleeList(email: String): PleeDictData {
        var existedPleeList = PleeDictData()
        (application as MasterApplication).service.GetPleelist(Token.token, ex_email)
            .enqueue(object : Callback<PleeDictData> {
                override fun onFailure(call: Call<PleeDictData>, t: Throwable) {
//                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
//                    Log.d("GetPleeList", "통신 오류")
                }

                override fun onResponse(
                    call: Call<PleeDictData>,
                    response: Response<PleeDictData>
                ) {
                    val result = response.body()
                    Log.d("GetPleeList", "2단계 통과:" + response.body())
                    Log.d("GetPleeList", "코드:" + response.code())
                    if (response.isSuccessful) {
                        existedPleeList.pleeList = response.body()!!.pleeList
                        Log.d("GetPleeList", "통신 성공")
                    }

                }
            })
        return existedPleeList
    }

    // POST 새로운 plee 정보 서버에 전달하기
    // PleeStateData(pleeName, completeCount) 보내기
    private fun PostPlee(pleestatedata: PleeStateData) {
        (application as MasterApplication).service.PostNowPlee(Token.token, ex_email, pleestatedata)
            .enqueue(object :
                Callback<PleeId> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeId>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("PostPlee", "통신 오류")
                }

                override fun onResponse(
                    call: Call<PleeId>,
                    response: Response<PleeId>
                ) {   // 통신 성공
                    val result = response.body()
                    Log.d("PostPlee", " " + result)
                    Log.d("PostPlee", " " + response.code())
                    if (response.isSuccessful) {
                        val result = response.body()
                    }

                }
            })
    }

    // POST ChcekStatus
    // SendPleeStatus(email, econame, pleename) 보내면 status(CMPLETE인지 GROWING인지) 받는 함수
    // isexsited: Boolean - 플리 유무를 확인하기 위해 플리가 있을 경우 True, 없을 경우 False
    private fun checkPleeStatus(sendpleestatus: SendPleeStatus): PleeStatus {
        var status = PleeStatus()
        Log.d("checkPleeStatus", "1단계 통과:" + sendpleestatus.ecoName)
        (application as MasterApplication).service.CheckStatus(Token.token, sendpleestatus)
            .enqueue(object :
                Callback<PleeStatus> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeStatus>,
                    t: Throwable
                ) {    // 통신 실패
                    Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("checkPleeStatus", "fail")
                }

                override fun onResponse(
                    call: Call<PleeStatus>,
                    response: Response<PleeStatus>
                ) {   // 통신 성공
                    val result = response.body()
                    Log.d("checkPleeStatus", "2단계 통과:" + response.body())
                    Log.d("checkPleeStatus", "코드:" + response.code())
                    if (response.isSuccessful) {
                        isexsited = true
                        status = result!!
                        Log.d("checkPleeStatus", "" + status.pleeStatus)
                        Log.d("checkPleeStatus", "" + status.pleeName)
                    } else {
                        isexsited = false
                        val converter: Converter<ResponseBody, LogInErrorMessage> =
                            (application as MasterApplication).retrofit.responseBodyConverter(
                                LogInErrorMessage::class.java, arrayOfNulls<Annotation>(0)
                            )

                        val error: LogInErrorMessage

                        try {
                            error = converter.convert(response.errorBody())!!
                            Log.e("error message", error.getErrorMessage())
                            Toast.makeText(
                                this@GrowUpPleeActivity,
                                error.getErrorMessage(),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        return status
    }

    // 이미지 아이디 불러오는 함수: random하게 불러오기 위해 필요한 함수
    private fun getResources(type: String, name: String): Int {
        return super.getResources().getIdentifier(type + name, null, packageName)
    }


}