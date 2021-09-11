package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.communication.*
import com.example.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_grow_up_plee.*
import kotlinx.android.synthetic.main.activity_plee_list.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import retrofit2.*
import java.io.IOException
import java.lang.StrictMath.random
import java.util.*


val tutorialnum: Long = 1// 튜토리얼 미션 횟수 저장 변수
val missionnum1: Long = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Long = 5// 2단계 미션 횟수 저장 변수
var use_growingPlee = GrowPleeData()


class GrowUpPleeActivity : AppCompatActivity() {
    private var allPleeList: Array<String> =
        arrayOf("ic_btn_add", "example_plant", "ic_btn_map")  // 예시 플리리스트 추후 수정 필요!!!
    private var tutorialPleeList: Array<String> =
        arrayOf("example_ducky", "example_ducky", "example_ducky")  // 튜토리얼 플리리스트 추후 수정 필요!!!

    private var isexsited = String()
    private var status = PleeStatus() // complete/growing 저장
    private var existedPleeList = PleeDictData() // COMPLETE 플리 리스트 Get


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)

        val sp_email = getSharedPreferences("user_email", Context.MODE_PRIVATE) // sp에서 값을 가져옴
        var email = sp_email.getString("user_email", "null")!!
        Log.d("UserEmail", email)

        // 코루틴 참고 사이트 : http://www.gisdeveloper.co.kr/?p=10279
        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.IO) {
                existedPleeList = GetPleeList(email)// COMPLETE 플리 리스트 Get
                GetGrowingPleeData(email)  // 자라는 플리 정보(플리 이름, 미션 진행 횟수) Get
            }.await()
            async(Dispatchers.Main) {
                delay(100)
                Log.d("asd", "" + use_growingPlee.ecoCount)
                test(email)
            }.await()


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

    fun test(email: String) {
        if (isexsited == "false") {  // 자라는 플리가 없을 때
            Log.d("isexsited", "false")
            //1) 새로운 플리 생성
            if (existedPleeList == null) { // COMPLETE plee 가 없다면 -> 튜토리얼 플리 생성
                Toast.makeText(this@GrowUpPleeActivity, "튜토리얼 플리를 성장시켜주세요!", Toast.LENGTH_LONG)
                    .show()
                val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                val tutorialTextView: TextView = findViewById(R.id.view_pleename)

                // 새로운 플리 생성
                val postdata = PleeStateData()
                val newPleeName = tutorialPleeList.random() // 플리 랜덤 추출
                postdata.pleeName = newPleeName
                postdata.completeCount = tutorialnum

                // 새로운 플리에 해당하는 이미지 찾기
                var pleeid: Int = getResources("drawable/", newPleeName)
                tutorialImageView.setImageResource(pleeid)
                tutorialTextView.setText(newPleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)

                PostPlee(postdata, email)
            } else {
                Toast.makeText(this@GrowUpPleeActivity, "새로운 플리를 만나볼까요?", Toast.LENGTH_LONG)
                    .show()
                val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                val tutorialTextView: TextView = findViewById(R.id.view_pleename)

                // 새로운 플리 생성
                val postdata = PleeStateData()
                val newPleeName = allPleeList.random() // 플리 랜덤 추출
                postdata.pleeName = newPleeName
                postdata.completeCount = tutorialnum

                // 새로운 플리에 해당하는 이미지 찾기
                var pleeid: Int = getResources("drawable/", newPleeName)
                tutorialImageView.setImageResource(pleeid)
                tutorialTextView.setText(newPleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)

                PostPlee(postdata, email)
            }

        } else { // 자라는 플리가 있을 때
            if (use_growingPlee.ecoCount!! < missionnum1) { // 0 --> 1단계 수행중
                Toast.makeText(this@GrowUpPleeActivity, "성장할고얏!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("1단계까지")
                var pleeid: Int = getResources("drawable", use_growingPlee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(use_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                var GrowingRate = (use_growingPlee.ecoCount!! / missionnum1) * 100
                progressbar.setProgress(GrowingRate.toInt())

            }
            // 2단계 이미지 바로 보여주면서 성장률 0퍼센트부터 시작
            else if (use_growingPlee.ecoCount!! == missionnum1) { // 1단계 도착
                Toast.makeText(this@GrowUpPleeActivity, "1단계 도착!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("2단계까지")
                var pleeid: Int = getResources("drawable", use_growingPlee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(use_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)
            } else if (use_growingPlee.ecoCount!! > missionnum1 && use_growingPlee.ecoCount!! < missionnum1 + missionnum2) { // 1 --> 2단계

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료")
                var pleeid: Int = getResources("drawable", use_growingPlee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(use_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                var GrowingRate = (use_growingPlee.ecoCount!! / missionnum1) * 100
                progressbar.setProgress(GrowingRate.toInt())
            } else if (use_growingPlee.ecoCount!! == missionnum1.toLong() + missionnum2.toLong()) { // 2단계까지 성장 완료

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료")
                var pleeid: Int = getResources("drawable", use_growingPlee.pleeName!!)
                GrowPleeImageView.setImageResource(pleeid)
                GrowPleeTextView.setText(use_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(100)
            }
        }
    }

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
    suspend fun GetGrowingPleeData(email: String) {
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
                    if (response.isSuccessful) {
                        val result = response.body()
                        isexsited = "true"
                        use_growingPlee.ecoCount = result?.ecoCount
                        use_growingPlee.pleeName = result?.pleeName
                        Log.d("GetGrowingPleeData", "ecoCount: " + use_growingPlee.ecoCount)
                        Log.d("GetGrowingPleeData", "ecoCount: " + use_growingPlee.pleeName)
                    } else {
                        isexsited = "false"
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

    }

    // 유저가 가지고 있는 플리 리스트 GET 함수
    // pleename: Stirng으로 구성된 Dict Get
    suspend fun GetPleeList(email: String): PleeDictData {
        var existedPleeList = PleeDictData()
        (application as MasterApplication).service.GetPleelist(Token.token, email)
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
    fun PostPlee(pleestatedata: PleeStateData, email: String) {
        (application as MasterApplication).service.PostNowPlee(Token.token, email, pleestatedata)
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
        return
    }

    // POST ChcekStatus == performEco, 미션 수행하는 함수
    // SendPleeStatus(email, econame, pleename) 보내면 status(COMPLETE인지 GROWING인지) 받는 함수
    fun checkPleeStatus(sendpleestatus: SendPleeStatus): PleeStatus {
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
                        status = result!!
                        Log.d("checkPleeStatus", "" + status.pleeStatus)
                        Log.d("checkPleeStatus", "" + status.pleeName)
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
        return status
    }

    // 이미지 아이디 불러오는 함수: random하게 불러오기 위해 필요한 함수
    private fun getResources(type: String, name: String): Int {
        return super.getResources().getIdentifier(type + name, null, packageName)
    }


}