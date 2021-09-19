package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.PleeListAdapter
import com.example.myapplication.communication.MasterApplication
import com.example.myapplication.communication.Token
import com.example.myapplication.utils.PleeList
import com.example.myapplication.utils.PleeName
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoActivity : AppCompatActivity() {

    private var pleeList = mutableListOf<PleeList>(
        PleeList("mr_min", "mr.민","NO.1", "mr_min_2", "버려진 병 속에서 자란 민들레지만\n누군가 후~ 불어주면\n날아서 전세계를 여행할거야!"),
        PleeList("ms_sun", "mr.선", "NO.2", "ms_sun_2", "안녕~ (@'ㅁ'@)\n나느은~ 선인장이야~ 만나서 반가워~"),
        PleeList("catni", "캣니", "NO.3", "catni_2", "시켜줘...\n 삼색이 명예\"닢\""),
        PleeList("happy", "해삐", "NO.4", "happy_2","너 생각보다 괜찮은 녀석이구나\n친구가 된다면 행복할거야"),
        PleeList("maji", "마지", "NO.5", "maji_2", "나는 달맞이 꽃이야~!\n\"기다림\"이라는 꽃말처럼\n지구가 깨끗해지길 기다리는 중이지"),
        PleeList("mas", "마스", "NO.6", "mas_2", "크리스마스가 365일이면 얼마나 좋을까!"),
        PleeList("mumo", "무모", "NO.7", "mumo_2", "나는야 멋쟁이 플리 될거야!\n환경을 아끼는 너처럼~!"),
        PleeList("pleetein", "플리틴", "NO.8", "pleetein_2", "난 작고 귀여운 플리였는데\n그냥 프로틴 먹고 3대 500 친 거 밖에 없어!"),
        PleeList("merge", "머지", "NO.9", "merge_2", "나는 궁금한 게 많은 머지야!\n원래는 해만 바라봤는데\n이제부터 날 키워준 너만을 바라볼까해")
    )

    private var pleeName = PleeName("pleename")
    private var existedPleeList = mutableListOf(pleeName) // COMPLETE 플리 리스트 Get


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Userinfo---", " start---")

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)
        setContentView(R.layout.activity_user_info)

        // 유저가 가지고 있는 플리 리스트 GET 함수
        // pleename: Stirng으로 구성된 Dict Get
        suspend fun UserGetPleeList(email: String): MutableList<PleeName> {
            (application as MasterApplication).service.GetPleelist(Token.token, email)
                .enqueue(object : Callback<MutableList<PleeName>> {
                    override fun onFailure(call: Call<MutableList<PleeName>>, t: Throwable) {
                        Toast.makeText(this@UserInfoActivity, "서버 통신 오류", Toast.LENGTH_LONG).show()
                        Log.d("UserGetPleeList---", "통신 오류")
                    }

                    override fun onResponse(
                        call: Call<MutableList<PleeName>>,
                        response: Response<MutableList<PleeName>>
                    ) {
                        val result = response.body()
                        Log.d("UserGetPleeList---", "2단계 통과:" + response.body())
                        Log.d("UserGetPleeList---", "2단계 통과:" + response.body()!!.size)

                        Log.d("UserGetPleeList---", "코드:" + response.code())
                        if (response.isSuccessful) {
                            existedPleeList = response.body()!!
                            Log.d("UserGetPleeList---", "통신 성공")
                            for (i in 0..response.body()!!.size - 1) {
                                existedPleeList[i].pleeName = response.body()!![i].pleeName
                            }
                        }
                    }
                })
            return existedPleeList
        }


        val sp_email = getSharedPreferences("user_email", Context.MODE_PRIVATE) // sp에서 값을 가져옴
        var email = sp_email.getString("user_email", "null")!!
        Log.d("UserEmail", email)

        // 코루틴 참고 사이트 : http://www.gisdeveloper.co.kr/?p=10279
        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.IO) {
                UserGetPleeList(email)// COMPLETE 플리 리스트 Get
            }.await()
            async(Dispatchers.Main) {
                delay(500)
                for (i in 0..existedPleeList.size - 1) {
                    Log.d("userinfo_getpleelist", ": " + existedPleeList[i].pleeName)
                }
                nonPlee() //없는 플리 리스트 가리
            }.await()
        }


        Userinfo_EcoGallery.setOnClickListener {
            var userinfo_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(userinfo_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Userinfo_Camera.setOnClickListener {
            var userinfo2camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(userinfo2camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Userinfo_Growup.setOnClickListener {
            var userinfo2camera_intent: Intent = Intent(this, GrowUpPleeActivity::class.java)
            startActivity(userinfo2camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Userinfo.setOnClickListener {
            var userinfo2userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(userinfo2userinfo_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }

    }

    suspend fun nonPlee(){
        var indexList2 = mutableListOf(-1) //index 비
        var pleeList2 = pleeList.toMutableList()

        for (i in 0..pleeList2.size - 1) {
            for (j in 0..existedPleeList.size - 1) {
                if (pleeList2[i].nameID == existedPleeList[j].pleeName) {
                    indexList2.add(i)
                }
            }
        }
        for (i in indexList2.size - 1 downTo 1) {
            Log.d("###indexList2---", ": " + indexList2[i])
            pleeList2.removeAt(indexList2[i])
        }

        for (i in 0..pleeList.size - 1) {
            for (j in 0..pleeList2.size - 1) {
                if (pleeList[i].nameID == pleeList2[j].nameID) {
                    pleeList[i].photo = ""
                }
            }
        }

        val mAdapter = PleeListAdapter(this, pleeList)
        rv_pleelist.adapter = mAdapter

        //3개열 gridlayout, 기본값(세로방향)
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        rv_pleelist.layoutManager = gridLayoutManager
    }
}