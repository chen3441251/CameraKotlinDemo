package com.demo.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_camera.setOnClickListener { checkAppPermission() }

    }

    private fun checkAppPermission() {
        val hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (hasCameraPermission === PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10010)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10010) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限允许
                takePhoto()
            } else {
                Toast.makeText(this, "拍照权限拒绝", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun takePhoto() {
        //新建文件保存路径
        val imageFile = File(externalCacheDir, "TestPhoto.jpg");
        if (imageFile.exists()) {
            imageFile.delete()
        }
        imageFile.createNewFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, packageName + ".provider", imageFile)
        } else {
            imageUri = Uri.fromFile(imageFile)
        }

        //调用拍照
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(cameraIntent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            2->{
                if(resultCode== Activity.RESULT_OK){
                iv_photo.setImageBitmap(BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri)))}
            }
            else->{Toast.makeText(this,"异常",Toast.LENGTH_LONG).show()}
        }
    }
}
