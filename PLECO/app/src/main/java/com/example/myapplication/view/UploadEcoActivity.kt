package com.example.myapplication.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.utils.ImageClassifier
import com.example.myapplication.utils.SetKey
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_upload_eco.*
import java.io.FileNotFoundException


class
UploadEcoActivity : AppCompatActivity() {
    private val CHOOSE_IMAGE = 1001
    private val labelList = ArrayList<String>()
    private lateinit var photoImage: Bitmap
    private lateinit var photoImageURI: Uri
    private lateinit var classifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_upload_eco)
        classifier = ImageClassifier(getAssets()) //이미지 분류기
        checkPermission()
        iv_none.setOnClickListener {
            choosePicture()
        }

        Camera_EcoGallery.setOnClickListener {
            var camera2ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(camera2ecogallery_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Camera_Camera.setOnClickListener {
            var camera2camera_intent: Intent = Intent(this, UploadEcoActivity::class.java)
            startActivity(camera2camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Camera_Growup.setOnClickListener {
            var camera2camera_intent: Intent = Intent(this, GrowUpPleeActivity::class.java)
            startActivity(camera2camera_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
        Camera_Userinfo.setOnClickListener {
            var camera2userinfo_intent: Intent = Intent(this, UserInfoActivity::class.java)
            startActivity(camera2userinfo_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.action = Intent.ACTION_GET_CONTENT
        } else {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE) // 갤러리 입장
        startActivityForResult(intent, CHOOSE_IMAGE) // 이미지 선택하여 전달됨


    }

    /*
    Bitmap.createScaleBitemap 은 매우 중요한 단계
    이를 거치지 않을 경우 .ArrayOutOfIndexException 이 발생할 수 있음
    샘플 이미지가 모델 이미지와 같은 크기여야 하기 때문
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {

            val uri: Uri = data!!.getData()!!
            val takeFlags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            }

            try {


                val stream = contentResolver!!.openInputStream(data!!.getData()!!)
                if (::photoImage.isInitialized) photoImage.recycle()
                photoImage = BitmapFactory.decodeStream(stream)
                photoImage = Bitmap.createScaledBitmap(
                    photoImage,
                    SetKey.INPUT_SIZE,
                    SetKey.INPUT_SIZE, false
                )


                classifier.recognizeImage(photoImage).subscribeBy(
                    onSuccess = {
                        for (i in 0 until it.size)
                            labelList.add(i, it[i].toString())
                    }
                )

                Log.d("트라이", "classifier")

                // Image URI 생성
                photoImageURI = data!!.getData()!!


                // Intent uploaded food activity
                var intent = Intent(this, UploadedEcoActivity::class.java)
                intent.putExtra("image", photoImage)
                intent.putStringArrayListExtra("label", labelList)
                intent.putExtra("uri", photoImageURI.toString())
                startActivity(intent)

                this@UploadEcoActivity.finish() // intent로 uploaded activity 넘어감과 동시에 activity 생명주기 종료

                overridePendingTransition(R.anim.horizon_exit, R.anim.none)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imageResult.setEnabled(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, ViewEcoActivity::class.java)
        startActivity(intent)
        this@UploadEcoActivity.finish()

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

    }


}