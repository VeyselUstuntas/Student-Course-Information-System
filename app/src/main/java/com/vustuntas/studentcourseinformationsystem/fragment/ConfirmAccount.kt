package com.vustuntas.studentcourseinformationsystem.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.adapter.UnapprovedAccountAdapter
import com.vustuntas.studentcourseinformationsystem.helperClass.UnapprovedAccount
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentConfirmAccountBinding
import java.lang.Exception
import java.util.ArrayList

class ConfirmAccount : Fragment() {
    private lateinit var _binding : FragmentConfirmAccountBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private lateinit var unapprovedAccount : ArrayList<UnapprovedAccount>
    private lateinit  var adapter : UnapprovedAccountAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmAccountBinding.inflate(inflater,container,false)
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null) }
        unapprovedAccount = ArrayList<UnapprovedAccount>()

        adapter = UnapprovedAccountAdapter(unapprovedAccount,database)
        val layoutManager = LinearLayoutManager(context)
        _binding.confirmAccountRecyclerView.layoutManager = layoutManager
        _binding.confirmAccountRecyclerView.adapter = adapter
        unapprovedAccount.clear()
        unapprovedAccounts()
    }


    private fun unapprovedAccounts(){
        try {
            val upprovedAccountCursor = database.rawQuery("SELECT l.approval, u.user_TRid, u.user_name, u.user_surname, u.user_title FROM Tbl_Login AS l JOIN Tbl_User AS u ON u.loginId = l.ID WHERE l.approval = 'false'",null)
            val approvalIndx = upprovedAccountCursor.getColumnIndex("approval")
            val accountIdIndx = upprovedAccountCursor.getColumnIndex("user_TRid")
            val accountNameIndx = upprovedAccountCursor.getColumnIndex("user_name")
            val accountSurnameIndx = upprovedAccountCursor.getColumnIndex("user_surname")
            val accountTitleIndx = upprovedAccountCursor.getColumnIndex("user_title")

            while (upprovedAccountCursor.moveToNext()){
                val unapprovedAccountObject = UnapprovedAccount(
                    upprovedAccountCursor.getString(approvalIndx),
                    upprovedAccountCursor.getString(accountIdIndx),
                    upprovedAccountCursor.getString(accountNameIndx),
                    upprovedAccountCursor.getString(accountSurnameIndx),
                    upprovedAccountCursor.getString(accountTitleIndx))

                unapprovedAccount.add(unapprovedAccountObject)
            }
            adapter.notifyDataSetChanged()
            upprovedAccountCursor.close()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }
}