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
import com.example.myapplication.communication.User
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
            ).enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@SignInActivity, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        val token = response.headers().get("Authorization").toString()
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