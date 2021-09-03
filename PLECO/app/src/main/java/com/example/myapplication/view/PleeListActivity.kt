package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_plee_list.*

class PleeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plee_list)

        overridePendingTransition(R.anim.none, R.anim.horizon_exit)

        pleelist_EcoGallery.setOnClickListener {
            var pleelist2ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(pleelist2ecogallery_intent)
            finish()

            overridePendingTransition(R.anim.none, R.anim.horizon_exit)

        }
        pleelist_Camera.setOnClickListener {
            var pleelist2camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(pleelist2camera_intent)
            finish()

            overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        }
        pleelist_Growup.setOnClickListener {
            var pleelist2growup_intent: Intent = Intent(this,GrowUpPleeActivity::class.java)
            startActivity(pleelist2growup_intent)
            finish()

            overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        }
        pleelist_Userinfo.setOnClickListener {
            var pleelist2userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(pleelist2userinfo_intent)
            finish()

            overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        }
    }




    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, GrowUpPleeActivity::class.java)
        startActivity(intent)
        this@PleeListActivity.finish()

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)
    }
}