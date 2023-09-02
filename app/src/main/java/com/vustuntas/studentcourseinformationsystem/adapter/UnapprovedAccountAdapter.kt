package com.vustuntas.studentcourseinformationsystem.adapter

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.UnapprovedAccount
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R

class UnapprovedAccountAdapter (var aprovalAccountArrayList: ArrayList<UnapprovedAccount>, val databaseObject : SQLiteDatabase) : RecyclerView.Adapter<UnapprovedAccountAdapter.UnaprovedAccountVH>() {
    class UnaprovedAccountVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    private val database : SQLiteDatabase = databaseObject
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnaprovedAccountVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_confirm_account,parent,false)
        return UnaprovedAccountVH(itemView)
    }

    override fun onBindViewHolder(holder: UnaprovedAccountVH, position: Int) {

        val classObject = aprovalAccountArrayList.get(position) as UnapprovedAccount
        holder.itemView.findViewById<TextView>(R.id.recyclerRow_confirmAccount_accountId).text = "User Id: ${classObject.accountId}"
        holder.itemView.findViewById<TextView>(R.id.recyclerRow_confirmAccount_accountName).text = "User Name: ${classObject.accountUserName}"
        holder.itemView.findViewById<TextView>(R.id.recyclerRow_confirmAccount_accountSurname).text = "User Surname : ${classObject.accountUserSurname}"
        holder.itemView.findViewById<TextView>(R.id.recyclerRow_confirmAccount_title).text = "User Title : ${classObject.accountTitle}"
        holder.itemView.findViewById<Button>(R.id.recyclerRow_confirmAccount_accountApprove_Button).setOnClickListener {
            try {
                val accountId = classObject.accountId
                val updateApprovalQuery = "UPDATE Tbl_Login SET approval = ? WHERE user_name = ?"
                val statment = database.compileStatement(updateApprovalQuery)
                statment.bindString(1,"true")
                statment.bindString(2,accountId)
                statment.execute()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return aprovalAccountArrayList.size
    }
}