package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_user_info)

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
}