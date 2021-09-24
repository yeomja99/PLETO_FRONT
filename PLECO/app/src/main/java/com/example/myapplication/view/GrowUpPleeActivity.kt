package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.communication.LogInErrorMessage
import com.example.myapplication.communication.MasterApplication
import com.example.myapplication.communication.Token
import com.example.myapplication.utils.*
import kotlinx.android.synthetic.main.activity_grow_up_plee.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import java.util.*


val tutorialnum: Long = 1// 튜토리얼 미션 횟수 저장 변수
val missionnum1: Long = 2// 1단계 미션 횟수 저장 변수
val missionnum2: Long = 5// 2단계 미션 횟수 저장 변수
var user_growingPlee = GrowPleeData()

class GrowUpPleeActivity : AppCompatActivity() {
    private var tutorialPleeList: Array<String> =
        arrayOf("mr_min")
    private var allPleeList: MutableList<String> =
        mutableListOf(
            "happy",
            "maji",
            "merge",
            "ms_sun",
            "mas",
            "catni",
            "mumo",
            "pleetein"
        )  // 튜토리얼 플리리스트 추후 수정 필요!!!

    private var isexsited = String() // 현재 자라는 플리가 있는지 없는지("true", "False" 문자형으로 저장)
    private var status = PleeStatus() // complete/growing 저장
    private var pleeName = PleeName("pleename")
    private var existedPleeList = mutableListOf(pleeName) // COMPLETE 플리 리스트 Get
    private var ecoName = String() // ecolabel 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)


        val sp_email = getSharedPreferences("user_email", Context.MODE_PRIVATE) // sp에서 값을 가져옴
        var email = sp_email.getString("user_email", "null")!!
        Log.d("UserEmail", email)
        var econameIntent: Intent = getIntent()

        Log.d("econame", "hasExtra: " + (econameIntent.hasExtra("eco_label")))
        if (econameIntent.hasExtra("eco_label")) {
            var kr_econame: String = econameIntent.getStringExtra("eco_label")
            Log.d("econame", "" + kr_econame)
            if (kr_econame == "에코백") ecoName = "ecoBag"
            else if (kr_econame == "텀블러") ecoName = "tumbler"
        } else Log.d("안됨", "")

        // 코루틴 참고 사이트 : http://www.gisdeveloper.co.kr/?p=10279
        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.IO) {
//                if (econameIntent.hasExtra("eco_label") == true) {
//                    status = checkPleeStatus(SendPleeStatus(email, ecoName))
//                    delay(500)
//                    GetGrowingPleeData(email)  // 자라는 플리 정보(플리 이름, 미션 진행 횟수) Get
//                    GetPleeList(email)// COMPLETE 플리 리스트 Get
//                } else {
//                    GetGrowingPleeData(email)  // 자라는 플리 정보(플리 이름, 미션 진행 횟수) Get
//                    GetPleeList(email)// COMPLETE 플리 리스트 Get
//                }

                status = checkPleeStatus(SendPleeStatus(email, "ecoBag"))
                delay(500)
                GetGrowingPleeData(email)  // 자라는 플리 정보(플리 이름, 미션 진행 횟수) Get
                GetPleeList(email)// COMPLETE 플리 리스트 Get

            }.await()
            async(Dispatchers.Main) {
                delay(500)
                Log.d("---isexsited", ": " + isexsited)
                GrowUpPleeFun(email)
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

    fun GrowUpPleeFun(email: String) {
        if (isexsited == "false") {  // 자라는 플리가 없을 때
            Log.d("isexsited", "" + isexsited)
            //1) 새로운 플리 생성
            if (existedPleeList.size == 0) { // COMPLETE plee 가 없다면 -> 튜토리얼 플리 생성
                Log.d("state", "exsitedPleeList: " + existedPleeList)
                Toast.makeText(this@GrowUpPleeActivity, "튜토리얼 플리를 성장시켜주세요!", Toast.LENGTH_LONG)
                    .show()
                val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                //val tutorialTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("2단계까지 0% 도달")
                // 새로운 플리 생성
                val postdata = PleeStateData()
                val newPleeName = tutorialPleeList[0] // 튜토리얼 플리
                postdata.pleeName = newPleeName
                postdata.completeCount = tutorialnum * 2
                Log.d("state", "새로운 플리 생성: " + newPleeName)

                // 새로운 플리에 해당하는 이미지 찾기
                var pleeid: Int = getResources("drawable/", newPleeName + "_0")
                tutorialImageView.setImageResource(pleeid)
                //tutorialTextView.setText(newPleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)

                PostPlee(postdata, email)
            } else if (existedPleeList.size > 0 && existedPleeList.size < allPleeList.size + tutorialPleeList.size) { // COMPLETE plee 가 있다면 -> 그거 제외하고 플리 생성

                val pleeImageView: ImageView = findViewById(R.id.view_plee)
                //val pleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)

                Log.d("state", "---existedPleeList: " + existedPleeList.size)
                Log.d("state", "---allpleelist: " + allPleeList.size)

                // 새로운 플리 생성
                val postdata = PleeStateData()

                val new_pleelist = allPleeList

                for (i in 0..existedPleeList.size - 1) {
                    val name: String = existedPleeList[i].pleeName!!
                    new_pleelist.remove(name)
                }

                val random = Random()
                val randomNum = random.nextInt(new_pleelist.size - 0)
                Log.d("state", "random num: " + randomNum)
                Log.d("state", ":" + new_pleelist.size)
                val newPleeName = new_pleelist[randomNum] // 플리 랜덤 추출

                postdata.pleeName = newPleeName
                postdata.completeCount = missionnum1 + missionnum2
                Log.d("state", "complete plee가 존재할 때: " + newPleeName)

                PostPlee(postdata, email)

                // 새로 생성한 플리 전에 완성된 플리 보여주기
                var cpleename: String = existedPleeList[existedPleeList.size - 1].pleeName!!
                var pleeid: Int = getResources("drawable/", cpleename + "_2")
                pleeImageView.setImageResource(pleeid)
                //pleeTextView.setText(cpleename)
                nextStateTextView.setText("성장 완료")

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(100)


            } else if (existedPleeList.size == allPleeList.size + tutorialPleeList.size) { // 모든 플리를 다 성장시킨 경우
                // 새로 생성한 플리 전에 완성된 플리 보여주기
                var cpleename: String = existedPleeList[existedPleeList.size - 1].pleeName!!
                var pleeid: Int = getResources("drawable/", cpleename + "_2")
                view_plee.setImageResource(pleeid)
                //pleeTextView.setText(cpleename)
                nextstate_textview.setText("성장 완료")

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(100)

                show_finish_plee.setText("엔딩 보기!")
                show_finish_plee.setBackgroundResource(getResources("drawable/","round_style"))

                show_finish_plee.setOnClickListener {
                    linearLayout1.removeView(state_bar)
                    linearLayout1.removeView(nextstate_textview)
                    linearLayout3.removeView(view_plee)
                    main_growup_view.removeView(show_finish_plee)
                    val finishImageView: ImageView = findViewById(R.id.allpleeview)
                    var pleeid: Int = getResources("drawable/", "allplee")
                    finishImageView.setImageResource(pleeid)

                    var progressbar: ProgressBar = state_bar
                    progressbar.setProgress(100)
                }
            }

        } else { // 자라는 플리가 있을 때
            // 튜토리얼 플리인 경우
            if (user_growingPlee.pleeName == "mr_min") {
                // 미션 완료 횟수가 0개일 경우
                if (user_growingPlee.ecoCount == 0.toLong()) {
                    Log.d("state", "tutorialplee: growing")
                    val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                    //val tutorialTextView: TextView = findViewById(R.id.view_pleename)
                    val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                    nextStateTextView.setText("2단계까지 0% 도달")

                    // 새로운 플리에 해당하는 이미지 찾기
                    var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName + "_0")
                    tutorialImageView.setImageResource(pleeid)
                    //tutorialTextView.setText(user_growingPlee.pleeName)

                    var progressbar: ProgressBar = state_bar
                    progressbar.setProgress(0)
                }
                // 미션 완료 횟수가 1개일 경우
                else if (user_growingPlee.ecoCount == 1.toLong()) {
                    Log.d("state", "tutorialplee: growing")
                    val tutorialImageView: ImageView = findViewById(R.id.view_plee)
                    //val tutorialTextView: TextView = findViewById(R.id.view_pleename)
                    val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                    nextStateTextView.setText("성장 완료까지 0% 도달")

                    // 새로운 플리에 해당하는 이미지 찾기
                    var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName + "_1")
                    tutorialImageView.setImageResource(pleeid)
                    //tutorialTextView.setText(user_growingPlee.pleeName)

                    var progressbar: ProgressBar = state_bar
                    progressbar.setProgress(0)
                }
            }
            // 튜토리얼 플리가 아닌 경우
            // 유저입장에서 1->2단계 수행중
            else if (user_growingPlee.ecoCount!! < missionnum1) { // 0 --> 1단계 수행중
                Toast.makeText(this@GrowUpPleeActivity, "성장할고얏!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                //val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                var GrowingRate = ((user_growingPlee.ecoCount!!) * 100) / missionnum1

                nextStateTextView.setText("2단계까지 " + GrowingRate.toInt().toString() + "% 도달")
                var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName!! + "_0")
                GrowPleeImageView.setImageResource(pleeid)
                //GrowPleeTextView.setText(user_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(GrowingRate.toInt())
                Log.d("state", "1단계 수행중(0-->1): " + GrowingRate)

            }
            // 2단계 이미지 바로 보여주면서 성장률 0퍼센트부터 시작
            // 유저 입장에서 2 -> 3단계(성장완료) 수행중
            else if (user_growingPlee.ecoCount!! == missionnum1) { // 2단계 도착
                Toast.makeText(this@GrowUpPleeActivity, "2단계 도착!", Toast.LENGTH_LONG)
                    .show()
                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                //al GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료까지 0% 도달")
                var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName!! + "_1")
                GrowPleeImageView.setImageResource(pleeid)
                //GrowPleeTextView.setText(user_growingPlee.pleeName)
                Log.d("state", "0단계 완료시 1단계 플리 생성: " + existedPleeList)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(0)
            }
            // 유저 입장에서 3->성장완료 단계 수행중
            else if (user_growingPlee.ecoCount!! > missionnum1 && user_growingPlee.ecoCount!! < missionnum1 + missionnum2) { // 1 --> 2단계

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                //val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                var GrowingRate = ((user_growingPlee.ecoCount!! - missionnum1) * 100) / missionnum2

                nextStateTextView.setText("성장 완료까지 " + GrowingRate.toInt().toString() + "% 도달")

                var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName!! + "_1")
                GrowPleeImageView.setImageResource(pleeid)
                //GrowPleeTextView.setText(user_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                Log.d("GrowingRate", ": " + GrowingRate)
                Log.d("state", "1단계 완료시 2단계 플리 생성: " + GrowingRate)

                progressbar.setProgress(GrowingRate.toInt())
            }
            // 유저 입장에서 성장 완료까지 모두 마침
            else if (user_growingPlee.ecoCount!! == missionnum1.toLong() + missionnum2.toLong()) { // 2단계까지 성장 완료

                val GrowPleeImageView: ImageView = findViewById(R.id.view_plee)
                //val GrowPleeTextView: TextView = findViewById(R.id.view_pleename)
                val nextStateTextView: TextView = findViewById(R.id.nextstate_textview)
                nextStateTextView.setText("성장 완료!")
                var pleeid: Int = getResources("drawable/", user_growingPlee.pleeName!! + "_2")
                GrowPleeImageView.setImageResource(pleeid)
                //GrowPleeTextView.setText(user_growingPlee.pleeName)

                var progressbar: ProgressBar = state_bar
                progressbar.setProgress(100)
            }
        }
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
                    //Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
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
                        Log.d("GetGrowingPleeData", "true == " + isexsited)
                        user_growingPlee.ecoCount = result?.ecoCount
                        user_growingPlee.pleeName = result?.pleeName
                        Log.d("GetGrowingPleeData", "ecoCount: " + user_growingPlee.ecoCount)
                        Log.d("GetGrowingPleeData", "ecoCount: " + user_growingPlee.pleeName)
                    } else {
                        isexsited = "false"
                        Log.d("GetGrowingPleeData", "false == " + isexsited)

                        val converter: Converter<ResponseBody, LogInErrorMessage> =
                            (application as MasterApplication).retrofit.responseBodyConverter(
                                LogInErrorMessage::class.java, arrayOfNulls<Annotation>(0)
                            )

                        val error: LogInErrorMessage

                        try {
                            error = converter.convert(response.errorBody())!!

                            Log.e("error message", error.getErrorMessage())
//                            Toast.makeText(
//                                this@GrowUpPleeActivity,
//                                error.getErrorMessage(),
//                                Toast.LENGTH_LONG
//                            ).show()

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })

    }

    // 유저가 가지고 있는 플리 리스트 GET 함수
    // pleename: Stirng으로 구성된 Dict Get
    suspend fun GetPleeList(email: String): MutableList<PleeName> {
        (application as MasterApplication).service.GetPleelist(Token.token, email)
            .enqueue(object : Callback<MutableList<PleeName>> {
                override fun onFailure(call: Call<MutableList<PleeName>>, t: Throwable) {
                    //Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("GetPleeList", "통신 오류")
                }

                override fun onResponse(
                    call: Call<MutableList<PleeName>>,
                    response: Response<MutableList<PleeName>>
                ) {
                    val result = response.body()
                    Log.d("GetPleeList", "2단계 통과:" + response.body())
                    Log.d("GetPleeList", "2단계 통과:" + response.body()!!.size)

                    Log.d("GetPleeList", "코드:" + response.code())
                    if (response.isSuccessful) {
                        existedPleeList = response.body()!!
                        Log.d("GetPleeList", "통신 성공")
                        for (i in 0..response.body()!!.size - 1) {
                            existedPleeList[i].pleeName = response.body()!![i].pleeName
                        }
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
                    //Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                    Log.d("PostPlee", "통신 오류")
                }

                override fun onResponse(
                    call: Call<PleeId>,
                    response: Response<PleeId>
                ) {   // 통신 성공
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("PostPlee", " " + result)
                        Log.d("PostPlee", " " + response.code())
                    } else {
                        val converter: Converter<ResponseBody, LogInErrorMessage> =
                            (application as MasterApplication).retrofit.responseBodyConverter(
                                LogInErrorMessage::class.java, arrayOfNulls<Annotation>(0)
                            )

                        val error: LogInErrorMessage

                        try {
                            error = converter.convert(response.errorBody())!!
//                            Log.e("error message", error.getErrorMessage())
//                            Toast.makeText(
//                                this@GrowUpPleeActivity,
//                                error.getErrorMessage(),
//                                Toast.LENGTH_LONG
//                            ).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
            })
        return
    }

    // POST ChcekStatus == performEco, 미션 수행하는 함수
    // SendPleeStatus(email, econame) 보내면 status(COMPLETE인지 GROWING인지) 받는 함수
    suspend fun checkPleeStatus(sendpleestatus: SendPleeStatus): PleeStatus {
        var status = PleeStatus()
        Log.d("checkPleeStatus", "1단계 통과:" + sendpleestatus.ecoName)
        (application as MasterApplication).service.CheckStatus(Token.token, sendpleestatus)
            .enqueue(object :
                Callback<PleeStatus> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(
                    call: Call<PleeStatus>,
                    t: Throwable
                ) {    // 통신 실패
                    //Toast.makeText(this@GrowUpPleeActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
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