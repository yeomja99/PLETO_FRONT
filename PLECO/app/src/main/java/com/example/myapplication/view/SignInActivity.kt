package com.example.myapplication.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.communication.LogInErrorMessage
import com.example.myapplication.communication.MasterApplication
import com.example.myapplication.communication.Token
import com.example.myapplication.communication.UserToken
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException


class SignInActivity : AppCompatActivity() {

    lateinit var logInBtn: TextView
    lateinit var goSignUpBtn: TextView
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initView(this@SignInActivity)

        goSignUpBtn.setOnClickListener {
            startActivity(
                Intent(this@SignInActivity, SignUpActivity::class.java)
            )
        }

        logInBtn.setOnClickListener {
            val email = getEmail()
            val password = getPassword()
            val body = HashMap<String, String>()
            body.put("email", email)
            body.put("password", password)

            if (email.toString() == "" || password.toString() == "") {
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
                        val info = response.body()  // token, success, pleeSize
                        val token = info!!.token.toString();   // token을 body에서 얻어오는 것으로 변경
                        val success = info!!.success
//                        val token = response.headers().get("Authorization").toString()  // user token
                        Log.d("UserToken", token)
                        Log.d("LoginSuccess", success.toString())
                        Log.d("LoginResponse", Gson().toJson(info))
                        if (success) {
                            saveUserToken(token, this@SignInActivity)
                            val sp = getSharedPreferences("login_token", Context.MODE_PRIVATE) // sp에서 값을 가져옴
                            val savedToken = sp.getString("login_token", "null")
                            if (savedToken != null) {
                                Token.token = savedToken
                            }
                            Log.d("SavedUserToken", savedToken)
                            (application as MasterApplication).createRetrofit()
                            Toast.makeText(this@SignInActivity, "환영합니다!", Toast.LENGTH_LONG).show()
                            startActivity(
                                Intent(this@SignInActivity, MainActivity::class.java)
                            )
                        }
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
                            Toast.makeText(this@SignInActivity, error.getErrorMessage(), Toast.LENGTH_LONG).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
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

    fun initView(activity: Activity) {
        logInBtn = activity.findViewById(R.id.login_btn)
        goSignUpBtn = activity.findViewById(R.id.go_signup_btn)
        email = activity.findViewById(R.id.signin_email)
        password = activity.findViewById(R.id.signin_password)
    }

    fun getEmail(): String{
        return email.text.toString()
    }

    fun getPassword(): String {
        return password.text.toString()
    }
}