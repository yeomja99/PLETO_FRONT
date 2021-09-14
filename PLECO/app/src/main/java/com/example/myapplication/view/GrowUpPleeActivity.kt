package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_grow_up_plee.*

class GrowUpPleeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        액티비티 전환 애니메이션 참고: https://greedy0110.tistory.com/52
        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_grow_up_plee)


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

        btn_move2pleelist.setOnClickListener {
            var move2pleelist_intent = Intent(this, PleeListActivity::class.java)
            startActivity(move2pleelist_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
    }
}