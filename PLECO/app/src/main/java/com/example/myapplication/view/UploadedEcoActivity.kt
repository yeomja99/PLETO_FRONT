package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.utils.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_uploaded_eco.*
import java.lang.reflect.Type
import com.bumptech.glide.RequestBuilder
import kotlinx.coroutines.delay

class UploadedEcoActivity : AppCompatActivity() {
    private val CHOOSE_IMAGE = 1001
    private var labelidx = 0
    private lateinit var imageUri : String
    private lateinit var photoImage: Bitmap
    private lateinit var labelList: ArrayList<String>

    // SAVE 버튼 --> 클릭 시 음식 사진, 음식 사진 id, 음식 이름을 배열에 저장 --> 객체 생성 해야 할듯
    // editText 버튼 --> 추측 된 이름이 마음에 들지 않을 경우 작명 가능

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.none, R.anim.horizon_exit)
        setContentView(R.layout.activity_uploaded_eco)
        photoImage = intent.getParcelableExtra("image")
        Log.d("이미지", photoImage.toString())
        labelList = intent.getStringArrayListExtra("label")
        Log.d("트라이", labelList.toString())
        imageUri = intent.getStringExtra("uri")
        Log.d("이미지in업로디드", imageUri)

       // Intent uploaded food activity
       var intentToUpload = Intent(this, UploadEcoActivity::class.java)
       var intentToGrowUpPleeActivity = Intent(this, GrowUpPleeActivity::class.java)

       if (labelList[0].equals("미션 실패")){
            intentToUpload.putExtra("fail",1)
            startActivity(intentToUpload)
            this@UploadedEcoActivity.finish()
        }

       if (intent.hasExtra("image") || intent.hasExtra("label") || intent.hasExtra("uri")) {
            // 1. 에코백, 텀블러가 85% 이상인 경우 미션 인정(mission 에 값부여)
            // Result.kt 에 예외처리

            // 3-1. 에코백, 텀블러일 경우 mission에 값을 넣음 (뒤에 intent putextra mission으로 수정)
            // 3-2. 에코백, 텀블러가 아닌 경우
            // 토스트 메시지로 에코백, 텀블러가 아님을 알림
            // intent UploadEcoActivity


            // 이미지 rotate 방지를 위한 코드
            val imguri = imageUri.toUri()
            Glide.with(this).load(imguri).override(500,500).fitCenter().into(iv_eco)

            //iv_eco.setImageBitmap(photoImage)
            tv_eco_name.text = labelList[labelidx]
        } else {
            Toast.makeText(this, "Image Error!", Toast.LENGTH_SHORT).show()
        }



    //       // Rename Eco Photo -> 추후 삭제
        /*tv_eco_name.setOnClickListener{
            labelidx ++
            if (labelidx < labelList.size)
                tv_eco_name.text = labelList[labelidx]
            else
                labelidx = 0
                tv_eco_name.text = labelList[labelidx]
        }*/


       // Save Image
       // Use Shared Preferences : string array
       // key(img uri) + food name
    //       btn_save.setOnClickListener{
    //           // Saves image URI as string to Default Shared Preferences
    //           var photos = ReadPhotosData()!!
    //           var dup = 0
    //
    //           Log.d("이미지 sp in Uploaded", photos.toString())
    //
    //           for (photo in ReadPhotosData()) {
    //               // 중복 저장 방지를 위한 mode 추가
    //               if (photo?.uri == imageUri)
    //                   dup = 1
    //               Log.d("이미지데이터 in Uploaded",photo?.uri + " : " + photo?.eco_id + "\n") // 잘 받아와 진당 ㅠㅠㅠㅠㅠㅠㅠ
    //           }
    //
    //           if (dup == 0 ){
    //               // 이미지가 sp에 없을 경우 저장
    //               photos.add(Photo(imageUri, labelList[labelidx]))
    //               SavePhotoData(photos)
    //               startActivity(intentToView)
    //           }
    //           else {
    //               Toast.makeText(this, "이미 존재하는 사진입니다.", Toast.LENGTH_LONG).show()
    //               startActivity(intentToUpload)
    //           }
    //
    //           this@UploadedEcoActivity.finish()
    //
    //
    //           overridePendingTransition(R.anim.horizon_exit, R.anim.none)
    //       }

       btn_retake_photo.setOnClickListener{
           startActivity(intentToUpload)
           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)
       }

       btn_save.setOnClickListener {

           // Saves image URI as string to Default Shared Preferences
           var photos = ReadPhotosData()!!
           var dup = 0

           Log.d("이미지 sp in Uploaded", photos.toString())

           for (photo in ReadPhotosData()) {
               // 중복 저장 방지를 위한 mode 추가
               if (photo?.uri == imageUri)
                   dup = 1
               Log.d("이미지데이터 in Uploaded",photo?.uri + " : " + photo?.eco_id + "\n") // 잘 받아와 진당 ㅠㅠㅠㅠㅠㅠㅠ
           }

           if (dup == 0 ){
               // 이미지가 sp에 없을 경우 저장
               photos.add(Photo(imageUri, labelList[labelidx]))
               SavePhotoData(photos)

               // label이 에코백 or 텀블러라고 판정날 경우 GrowUp으로 전송
               intentToGrowUpPleeActivity.putExtra("eco_label", labelList[labelidx])
               startActivity(intentToGrowUpPleeActivity)
           }
           else {
               Toast.makeText(this, "이미 존재하는 사진입니다.", Toast.LENGTH_LONG).show()
               startActivity(intentToUpload)
           }

           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)

       }

       Camera2_EcoGallery.setOnClickListener {
           var camera22gallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
           startActivity(camera22gallery_intent)
           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)

       }

       Camera2_Camera.setOnClickListener {
           var camera22camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
           startActivity(camera22camera_intent)
           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)

       }
       Camera2_Growup.setOnClickListener {
           var camera22growup_intent: Intent = Intent(this, GrowUpPleeActivity::class.java)
           startActivity(camera22growup_intent)
           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)

       }
       Camera2_Userinfo.setOnClickListener {
           var camera22userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
           startActivity(camera22userinfo_intent)
           this@UploadedEcoActivity.finish()

           overridePendingTransition(R.anim.horizon_exit, R.anim.none)

       }




//        // Intent RecommendFoodActivity
//        btn_search.setOnClickListener{
//            startActivity(intentToRecommend)
//        }


    }


    private fun SavePhotoData(Photos: ArrayList<Photo?>?) {
        val preferences: SharedPreferences = getSharedPreferences("PHOTO_LIST", Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(Photos)
        editor.putString("PHOTO_LIST", json) // json 타입으로 변환한 객체 저장
        editor.commit() // 완료
    }


    private fun ReadPhotosData(): ArrayList<Photo?> {
        val sharedPrefs: SharedPreferences = getSharedPreferences("PHOTO_LIST", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString("PHOTO_LIST", "EMPTY")
        val type: Type = object : TypeToken<ArrayList<Photo?>?>() {}.type
        if (json.toString() != "EMPTY")
            return gson.fromJson(json, type) //Array List 반환

        var photos = ArrayList<Photo?>()
        return photos
    }


    override fun onBackPressed() {
        super.onBackPressed()
        this@UploadedEcoActivity.finish()

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)
    }
}