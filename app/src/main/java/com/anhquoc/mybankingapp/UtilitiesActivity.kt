package com.anhquoc.mybankingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class UtilitiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utilities)

        // Ánh xạ các nút
        val btnElectricity = findViewById<Button>(R.id.btnElectricity)
        val btnWater = findViewById<Button>(R.id.btnWater)
        val btnInternet = findViewById<Button>(R.id.btnInternet)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Xử lý sự kiện bấm nút
        btnElectricity.setOnClickListener {
            showPaymentDialog("Tiền Điện")
        }

        btnWater.setOnClickListener {
            showPaymentDialog("Tiền Nước")
        }

        btnInternet.setOnClickListener {
            showPaymentDialog("Internet")
        }

        btnBack.setOnClickListener {
            finish() // Đóng màn hình này để quay lại Main
        }
    }

    // Hàm hiển thị hộp thoại thanh toán giả lập
    private fun showPaymentDialog(serviceName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thanh toán $serviceName")
        builder.setMessage("Nhập mã khách hàng (Mã hóa đơn):")

        // Tạo ô nhập liệu
        val input = EditText(this)
        input.hint = "Ví dụ: PE12345678"
        builder.setView(input)

        builder.setPositiveButton("Thanh toán") { _, _ ->
            val code = input.text.toString()
            if (code.isNotEmpty()) {
                // Giả lập xử lý thành công
                Toast.makeText(this, "Đã thanh toán $serviceName cho mã $code thành công!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Vui lòng nhập mã khách hàng", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy", null)
        builder.show()
    }
}