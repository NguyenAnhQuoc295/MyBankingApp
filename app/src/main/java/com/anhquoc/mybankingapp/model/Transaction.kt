package com.anhquoc.mybankingapp.model

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val type: String = "Transfer",
    val date: String = "",
    val status: String = "Success"
)