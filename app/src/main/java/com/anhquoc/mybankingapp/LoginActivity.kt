package com.anhquoc.mybankingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvError: TextView // Khai báo biến toàn cục để dễ dùng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Khởi tạo Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Ánh xạ View
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)

        // Xử lý sự kiện nút Đăng nhập
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim() // Thêm trim() để xóa khoảng trắng thừa
            val pass = edtPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Thực hiện đăng nhập với Firebase
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Đăng nhập thành công -> Kiểm tra quyền (Role)
                            checkUserRole()
                        } else {
                            // Đăng nhập thất bại
                            tvError.text = "Lỗi: Sai email hoặc mật khẩu!"
                        }
                    }
            } else {
                tvError.text = "Vui lòng nhập đầy đủ thông tin!"
            }
        }
    }

    // Hàm kiểm tra xem user là "customer" hay "officer"
    private fun checkUserRole() {
        val userId = auth.currentUser?.uid ?: return

        // Truy cập vào node: users/{userId}/profile/role
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId/profile/role")

        dbRef.get().addOnSuccessListener { snapshot ->
            val role = snapshot.value.toString()

            if (role == "officer") {
                // TRƯỜNG HỢP: NHÂN VIÊN NGÂN HÀNG
                Toast.makeText(this, "Chào mừng cán bộ ngân hàng", Toast.LENGTH_SHORT).show()

                // Sau này bạn tạo OfficerActivity xong thì bỏ comment dòng dưới để chạy nhé:
                // startActivity(Intent(this, OfficerActivity::class.java))

            } else {
                // TRƯỜNG HỢP: KHÁCH HÀNG (Mặc định hoặc role="customer")
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish() // Đóng LoginActivity để không quay lại được bằng nút Back

        }.addOnFailureListener {
            // Trường hợp lỗi mạng hoặc chưa có data role -> Mặc định cho vào MainActivity
            Toast.makeText(this, "Không lấy được thông tin role, vào mặc định!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}