package com.example.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.utils.PleeList
import kotlinx.android.synthetic.main.activity_plee_info.*

class PleeInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plee_info)

        var receiverData1 = intent.getStringExtra("image_name")
        var receiverData2 = intent.getStringExtra("plee_info")
        var receiverData3 = intent.getStringExtra("plee_name")

        if (receiverData1 != "") {
            val resourceId = resources.getIdentifier(receiverData1, "drawable", packageName)

            if (resourceId > 0) {
                iv_info.setImageResource(resourceId)
                tv_info.setText(receiverData3)
                tv_info_plee.setText(receiverData2)
            }
            else {
                iv_info.setImageResource(R.mipmap.ic_launcher_round)
            }
        }
        else {
            iv_info.setImageResource(R.drawable.ic_nonplee)
            tv_info.setText("???")
            tv_info_plee.setText("???")
            Toast.makeText(this@PleeInfoActivity, "아직 얻지 못했어...", Toast.LENGTH_SHORT).show()
        }
    }
}