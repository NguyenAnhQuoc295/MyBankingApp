package com.anhquoc.mybankingapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView

    // Tạo bộ lắng nghe kết quả khi người dùng chọn ảnh xong
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data

            // Hiển thị ảnh vừa chọn lên màn hình
            if (imageUri != null) {
                imgAvatar.setImageURI(imageUri)
                Toast.makeText(this, "Xác thực eKYC thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgAvatar = findViewById(R.id.imgAvatar)
        val btnUploadFace = findViewById<Button>(R.id.btnUploadFace)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Sự kiện nút Upload eKYC
        btnUploadFace.setOnClickListener {
            openGallery()
        }

        btnBack.setOnClickListener {
            finish() // Đóng màn hình
        }
    }

    private fun openGallery() {
        // Mở thư viện ảnh trên điện thoại
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }
}