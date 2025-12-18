package com.anhquoc.mybankingapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anhquoc.mybankingapp.model.Account
import com.anhquoc.mybankingapp.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // 1. Khai báo biến
    private lateinit var rvAccounts: RecyclerView
    private lateinit var edtAmount: EditText
    private lateinit var btnTransfer: Button
    private lateinit var tvStatus: TextView
    private lateinit var btnUtilities: Button
    private lateinit var btnMap: Button
    private lateinit var btnProfile: Button

    // Biến cho RecyclerView
    private val accountList = mutableListOf<Account>()
    private lateinit var adapter: AccountAdapter

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Ánh xạ View
        rvAccounts = findViewById(R.id.rvAccounts)
        edtAmount = findViewById(R.id.edtAmount)
        btnTransfer = findViewById(R.id.btnTransfer)
        tvStatus = findViewById(R.id.tvStatus)
        btnUtilities = findViewById(R.id.btnUtilities)
        btnMap = findViewById(R.id.btnMap)
        btnProfile = findViewById(R.id.btnProfile)

        // 3. Cấu hình RecyclerView
        rvAccounts.layoutManager = LinearLayoutManager(this)
        adapter = AccountAdapter(accountList)
        rvAccounts.adapter = adapter

        // 4. Lấy dữ liệu tài khoản từ Firebase
        fetchAccounts()

        // 5. Xử lý sự kiện nút Chuyển tiền
        btnTransfer.setOnClickListener {
            val amountStr = edtAmount.text.toString()
            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                handleTransaction(amount)
            } else {
                tvStatus.text = "Vui lòng nhập số tiền!"
                tvStatus.setTextColor(Color.RED)
            }
        }

        // 6. Các nút điều hướng
        btnUtilities.setOnClickListener {
            startActivity(Intent(this, UtilitiesActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnMap.setOnClickListener {
            // Chuyển sang MapsActivity (Bạn cần tạo Activity này ở bước sau)
            // Nếu chưa tạo thì comment dòng dưới lại để không lỗi
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    // --- HÀM LOGIC ---

    // Hàm lấy danh sách tài khoản realtime
    private fun fetchAccounts() {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("users/$userId/accounts")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accountList.clear()
                for (item in snapshot.children) {
                    val acc = item.getValue(Account::class.java)
                    if (acc != null) {
                        // Gán key của node vào thuộc tính id để dùng cập nhật sau này
                        acc.id = item.key.toString()
                        accountList.add(acc)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hàm xử lý logic trước khi chuyển
    private fun handleTransaction(amount: Double) {
        // 1. Tìm tài khoản nguồn (Mặc định lấy tài khoản Checking - Thanh toán)
        val sourceAccount = accountList.find { it.type == "Checking" }

        if (sourceAccount == null) {
            tvStatus.text = "Bạn không có tài khoản thanh toán (Checking)!"
            return
        }

        // 2. Kiểm tra số dư
        if (amount <= 0) {
            tvStatus.text = "Số tiền không hợp lệ!"
            return
        }
        if (sourceAccount.balance < amount) {
            tvStatus.text = "Số dư không đủ để thực hiện giao dịch!"
            return
        }

        // 3. Giả lập OTP (Hoặc gọi Biometric nếu số tiền lớn)
        val otpCode = (100000..999999).random().toString()
        showOTPDialog(otpCode, amount, sourceAccount)
    }

    // Hộp thoại nhập OTP
    private fun showOTPDialog(correctOtp: String, amount: Double, account: Account) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác thực OTP (2FA)")
        builder.setMessage("Mã OTP của bạn là: $correctOtp\nNhập mã này để xác nhận chuyển khoản.")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("Xác nhận") { _, _ ->
            if (input.text.toString() == correctOtp) {
                // OTP đúng -> Thực hiện trừ tiền
                performTransfer(amount, account)
            } else {
                Toast.makeText(this, "Sai mã OTP!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    // Hàm trừ tiền và lưu lịch sử lên Firebase
    private fun performTransfer(amount: Double, account: Account) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Tính số dư mới
        val newBalance = account.balance - amount

        // 2. Cập nhật số dư trên Firebase
        val accountRef = database.getReference("users/$userId/accounts/${account.id}/balance")
        accountRef.setValue(newBalance)
            .addOnSuccessListener {
                // 3. Nếu cập nhật số dư thành công -> Lưu lịch sử giao dịch
                saveTransactionHistory(amount, account.accountNumber)

                tvStatus.text = "Giao dịch thành công!"
                tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Màu xanh
                edtAmount.text.clear()

                Toast.makeText(this, "Đã chuyển tiền thành công!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                tvStatus.text = "Lỗi hệ thống, vui lòng thử lại!"
            }
    }

    // Hàm lưu lịch sử giao dịch
    private fun saveTransactionHistory(amount: Double, fromAccountNum: String) {
        val transRef = database.getReference("transactions")
        val transId = transRef.push().key // Tạo ID ngẫu nhiên
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val transaction = Transaction(
            id = transId ?: "",
            amount = amount,
            content = "Chuyen tien qua app",
            date = dateStr,
            fromAccount = fromAccountNum,
            toAccount = "OUTSIDE", // Giả sử chuyển ra ngoài
            status = "Success",
            type = "Transfer"
        )

        if (transId != null) {
            transRef.child(transId).setValue(transaction)
        }
    }
}