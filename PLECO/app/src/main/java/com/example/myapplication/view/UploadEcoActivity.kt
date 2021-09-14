package com.example.myapplication.view

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.R
import com.example.myapplication.utils.ImageClassifier
import com.example.myapplication.utils.SetKey
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_upload_eco.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class
UploadEcoActivity : AppCompatActivity() {
//    private val CHOOSE_IMAGE = 1001
    private val labelList = ArrayList<String>()
    private lateinit var photoImage: Bitmap
    private lateinit var photoImageURI: Uri
    private lateinit var classifier: ImageClassifier
    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        setContentView(R.layout.activity_upload_eco)
        classifier = ImageClassifier(getAssets()) //이미지 분류기

        iv_none.setOnClickListener {
//            choosePicture() //기존에 있던 갤러리 부르는 코드
            // 1. 카메라로 찍기
            if (checkPersmission()){
                dispatchTakePictureIntent()
            }else{
                requestPermission()
            }
            // 2. 카메라로 찍은 사진 확인
            // 3. 찍은 사진을 Classifier에 전송
        }

        Camera_EcoGallery.setOnClickListener {
            var camera2ecogallery_intent: Intent = Intent(this, ViewEcoActivity::class.java)
            startActivity(camera2ecogallery_intent)
            finish()

            overridePendingTransition(R.anim.horizon_exit, R.anim.none)

        }

        Camera_Camera.setOnClickListener {
            // Camera Activity로 넘어가는 버튼
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

    // 카메라 권한 요청
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), REQUEST_IMAGE_CAPTURE)
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

//    private fun choosePicture() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            intent.action = Intent.ACTION_GET_CONTENT
//        } else {
//            intent.action = Intent.ACTION_OPEN_DOCUMENT
//        }
//        intent.addCategory(Intent.CATEGORY_OPENABLE) // 갤러리 입장
//        startActivityForResult(intent, CHOOSE_IMAGE) // 이미지 선택하여 전달됨
//    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("TAG", "그림파일 만드는도중 에러생김")
                        null
                    }

                // 그림파일을 성공적으로 만들었다면 onActivityForResult로 보내기
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this, "com.example.myapplication.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    lateinit var currentPhotoPath: String

    // 카메라로 촬영한 이미지를 파일로 저장해준다
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            Log.d("test", "currentPhotoPath2 : $currentPhotoPath")
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    /*
    Bitmap.createScaleBitemap 은 매우 중요한 단계
    이를 거치지 않을 경우 .ArrayOutOfIndexException 이 발생할 수 있음
    샘플 이미지가 모델 이미지와 같은 크기여야 하기 때문
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {


            //Bitmap 형식으로 이미지 불러오는 듯
//            val imageBitmap = data!!.extras!!.get("data") as Bitmap


            // 카메라로부터 받은 데이터가 있을경우에만
            val file = File(currentPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))  //Deprecated
            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                    Uri.fromFile(file))
                val bitmap = ImageDecoder.decodeBitmap(decode)
            }

            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              ////갤러리에 접근 가능 여부를 얻기 위한 부분
//                contentResolver.takePersistableUriPermission(Uri.fromFile(file), takeFlags)
//            }

            try {


                val stream = contentResolver!!.openInputStream(Uri.fromFile(file))
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
                photoImageURI = Uri.fromFile(file)


                // Intent uploaded eco activity
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