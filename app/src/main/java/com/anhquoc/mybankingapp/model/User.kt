package com.anhquoc.mybankingapp.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "customer", // "customer" hoặc "officer" [cite: 20]
    val avatarUrl: String = ""
)