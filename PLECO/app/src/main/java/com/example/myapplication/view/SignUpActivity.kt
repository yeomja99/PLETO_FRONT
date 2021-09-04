package com.example.myapplication.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.communication.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    lateinit var nicknameView: EditText
    lateinit var password1View: EditText
    lateinit var password2View: EditText
    lateinit var registerBtn: TextView
    lateinit var nicknameOkBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initView(this@SignUpActivity)

        // 닉네임 중복 확인 버튼
        nicknameOkBtn.setOnClickListener {
            nicknameCheck(this)
        }

        // 가입하기 버튼
        signup_btn.setOnClickListener {
            register(this)
        }
    }

    fun register(activity: Activity) {
        val nickname = getNickName()
        val password1 = getPassword1()
        val password2 = getPassword2()
        val user = UserInfo(nickname, password1)

        if (nickname != "" && password1 != "" && password2 != "") {
            // 비밀번호 일치 확인
            if (password1 != password2) {
                Toast.makeText(activity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show()
            } else {
                (application as MasterApplication).service.register(user)
                    .enqueue(object : Callback<SignUpOkCheck> {  // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                        override fun onFailure(call: Call<SignUpOkCheck>, t: Throwable) {    // 통신 실패
                            Toast.makeText(activity, "회원가입에 실패했습니다", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<SignUpOkCheck>, response: Response<SignUpOkCheck>) {   // 통신 성공
                            val status = response.body()
                            val success = status!!.success
                            if(success)
                                Toast.makeText(activity, "회원가입에 성공했습니다", Toast.LENGTH_LONG).show()
//                            saveUserToken(token, activity)
//                            (application as MasterApplication).createRetrofit()
                            activity.startActivity(
                                Intent(activity, SignInActivity::class.java)
                            )
                        }
                    })
            }
        } else {    // 필요한 정보가 누락 됐을 경우
            Toast.makeText(activity, "회원가입 정보를 입력해주세요", Toast.LENGTH_LONG).show()
        }
    }

    // 닉네임 중복 확인 함수
    fun nicknameCheck(activity: Activity) {
        val nickname = getNickName()

        if (nickname == "") {
            Toast.makeText(activity, "닉네임을 입력해주세요", Toast.LENGTH_LONG).show()
        } else {
            (application as MasterApplication).service.getNicknameIsExist(nickname)
                .enqueue(object : Callback<Email> {
                    override fun onFailure(call: Call<Email>, t: Throwable) {   // 통신 실패
                        Toast.makeText(activity, "닉네임 중복 확인에 실패했습니다", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<Email>, response: Response<Email>) { // 통신 성공
                        if (response.isSuccessful) {
                            val result = response.body()
                            val success = result!!.success!!
                            // 닉네임 중복
                            if (!success) {
                                Toast.makeText(activity, "사용 불가능한 닉네임입니다", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "사용 가능한 닉네임입니다", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
        }

    }

    // 토큰 받아서 SharedPreference에 저장
    fun saveUserToken(token: String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_token", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_token", token)
        editor.commit()
    }

    fun initView(activity: Activity) {
        nicknameView = activity.findViewById(R.id.nickname_inputbox)
        password1View = activity.findViewById(R.id.password1_inputbox)
        password2View = activity.findViewById(R.id.password2_inputbox)
        registerBtn = activity.findViewById(R.id.signup_btn)
        nicknameOkBtn = activity.findViewById(R.id.nickname_ok_btn)
    }

    fun getNickName(): String{
        return nicknameView.text.toString()
    }

    fun getPassword1(): String {
        return password1View.text.toString()
    }

    fun getPassword2(): String {
        return password2View.text.toString()
    }

}