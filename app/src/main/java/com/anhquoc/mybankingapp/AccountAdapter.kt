package com.anhquoc.mybankingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anhquoc.mybankingapp.model.Account
import java.text.DecimalFormat

class AccountAdapter(private val accountList: List<Account>) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    // Định dạng tiền tệ: 1000000 -> 1,000,000 VND
    private val currencyFormat = DecimalFormat("#,### VND")

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tvAccountType)
        val tvBalance: TextView = itemView.findViewById(R.id.tvAccountBalance)
        val tvNumber: TextView = itemView.findViewById(R.id.tvAccountNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val acc = accountList[position]

        // Hiển thị dữ liệu
        // Sửa trực tiếp logic hiển thị tại đây
        val typeName = if (acc.type == "Checking") "Tài khoản thanh toán" else "Tài khoản tiết kiệm"
        holder.tvType.text = typeName.uppercase()
        holder.tvBalance.text = currencyFormat.format(acc.balance)
        holder.tvNumber.text = "STK: ${acc.accountNumber}"

        // Đổi màu số dư nếu là Saving (Ví dụ màu xanh dương)
        if (acc.type == "Saving") {
            holder.tvType.setTextColor(android.graphics.Color.BLUE)
        }
    }

    override fun getItemCount() = accountList.size
}