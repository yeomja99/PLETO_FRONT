package com.example.myapplication.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.communication.MasterApplication
import com.example.myapplication.communication.UserInfo
import com.example.myapplication.communication.UserToken
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        go_signup_btn.setOnClickListener {
            startActivity(
                Intent(this@SignInActivity, SignUpActivity::class.java)
            )
        }

        login_btn.setOnClickListener {
            val nickname = signin_nickname.text.toString()
            val password = signin_password.text.toString()
            val body = HashMap<String, String>()
            body.put("nickname", nickname)
            body.put("password", password)

            if (nickname == "" || password == "") {
                Toast.makeText(this@SignInActivity, "로그인 정보를 입력해주세요", Toast.LENGTH_LONG).show()
            }

            (application as MasterApplication).service.login(
                body
            ).enqueue(object : Callback<UserToken> { // ! Callback 은 반드시 retrofit 의 Callback 을 사용할 것 !
                override fun onFailure(call: Call<UserToken>, t: Throwable) {    // 통신 실패
                    Toast.makeText(this@SignInActivity, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserToken>, response: Response<UserToken>) {   // 통신 성공
                    if (response.isSuccessful) {
                        val user = response.body()  // email, password, token
                        val token = user!!.token;   // token을 body에서 얻어오는 것으로 변경
//                        val token = response.headers().get("Authorization").toString()  // user token
                        Log.d("user", token)
                        Log.d("User", user.toString())
                        saveUserToken(token, this@SignInActivity)
                        (application as MasterApplication).createRetrofit()
                        Toast.makeText(this@SignInActivity, "환영합니다!", Toast.LENGTH_LONG).show()
                        startActivity (
                            Intent(this@SignInActivity, MainActivity::class.java)
                        )
                    }
                }
            })

        }
    }

    fun saveUserToken(token: String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_token", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_token", token)
        editor.commit()
    }
}