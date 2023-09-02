package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.activity.MainActivity
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAccountLogOutBinding

class AccountLogOut : Fragment() {
    private lateinit var _bindig : FragmentAccountLogOutBinding
    private val binding get() = _bindig.root
    private lateinit var database : SQLiteDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindig = FragmentAccountLogOutBinding.inflate(inflater,container,false)
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            database.execSQL("DELETE FROM Tbl_LastAccount")
            val intent = Intent(context,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
            Toast.makeText(context,"Exit Made", Toast.LENGTH_LONG).show()

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

}