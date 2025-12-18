package com.anhquoc.mybankingapp.model

data class Account(
    var id: String = "",
    val accountNumber: String = "",
    val type: String = "Checking", // Checking, Saving
    val balance: Double = 0.0,
    val interestRate: Double = 0.0,
    val createdDate: String = ""
) {
    // --- BẠN ĐANG THIẾU ĐOẠN NÀY ---
    fun getDisplayName(): String {
        return if (type == "Checking") "Tài khoản thanh toán" else "Tài khoản tiết kiệm"
    }
}