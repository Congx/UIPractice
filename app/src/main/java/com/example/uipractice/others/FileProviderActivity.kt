package com.example.uipractice.others

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.base.rxjavalib.bindLifecycle
import com.example.uipractice.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_file_provider.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


var REQ_CODE = 0
class FileProviderActivity : AppCompatActivity() {

    lateinit var mImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_provider)

        btnCamera.setOnClickListener {
            RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .bindLifecycle(this)
                .subscribe {
                    if (it) {
                        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //用来打开相机的Intent

                        if (takePhotoIntent.resolveActivity(packageManager) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
//                            startActivityForResult(takePhotoIntent, REQ_CODE) //启动相机
                            takePhoto()
                        }
                    }
                }
        }

        btnPhoto.setOnClickListener {
            RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .bindLifecycle(this)
                .subscribe {
                    if (it) {
                        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //用来打开相机的Intent

                        if (takePhotoIntent.resolveActivity(packageManager) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
//                            startActivityForResult(takePhotoIntent, REQ_CODE) //启动相机
                            takePhoto()
                        }
                    }
                }
        }

        btnfile.setOnClickListener {
            RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .bindLifecycle(this)
                .subscribe {
                    selectPicFromLocal()
                }

        }
    }

    val VALUE_PICK_PICTURE = 2
    private fun selectPicFromLocal() {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, VALUE_PICK_PICTURE)
    }

    private fun takePhoto() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        if (takePhotoIntent.resolveActivity(packageManager) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
            val imageFile: File? = createImageFile() //创建用来保存照片的文件
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    mImageUri = FileProvider.getUriForFile(this, "com.example.uipractice.fileprovider", imageFile)
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    mImageUri = Uri.fromFile(imageFile)
                }
                takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri) //将用于输出的文件Uri传递给相机
                startActivityForResult(takePhotoIntent, 1) //打开相机
            }
        }
    }

    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     * @return 创建的图片文件
     */
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
        val imageFileName = "photo.jpg"
//        val file: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(Environment.getExternalStorageDirectory(), "aaa/jjj")
//        val file = Environment.getExternalStorageDirectory()
        if (!file.exists()) {
            file.mkdirs()
        }
        var imageFile: File? = null
        try {
            imageFile = File(file,imageFileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
//        if (requestCode === REQ_CODE && resultCode === Activity.RESULT_OK) {
//            /*缩略图信息是储存在返回的intent中的Bundle中的，
//            * 对应Bundle中的键为data，因此从Intent中取出
//            * Bundle再根据data取出来Bitmap即可*/
//            val extras: Bundle? = data?.getExtras()
//            val bitmap: Bitmap? = extras?.get("data") as Bitmap?
//            image.setImageBitmap(bitmap)
//        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                val openInputStream = contentResolver.openInputStream(mImageUri)
                val bitmap: Bitmap = BitmapFactory.decodeStream(openInputStream)
                image.setImageBitmap(bitmap) //显示到ImageView上
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

}
