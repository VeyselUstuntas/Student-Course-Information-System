package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.adapter.ChooseLecturerAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentChooseLecturerBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.LecturerInfo
import java.util.ArrayList
import java.util.SplittableRandom

class ChooseLecturer : Fragment() {
    private lateinit var _binding : FragmentChooseLecturerBinding
    private val binding get() = _binding.root
    private lateinit var database :SQLiteDatabase
    private lateinit var lecturerInfolist : ArrayList<LecturerInfo>
    private lateinit var adapter : ChooseLecturerAdapter
    private lateinit var courseInfoArray : Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseLecturerBinding.inflate(inflater,container,false)
        activity?.let {  database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)}

        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lecturerInfolist = ArrayList<LecturerInfo>()
        lecturerInfolist.clear()
        getLecturerInfo()
        arguments?.let {
            courseInfoArray = ChooseLecturerArgs.fromBundle(it).courseTransInfo

        }
        val layoutManager = LinearLayoutManager(context)
        adapter = ChooseLecturerAdapter(lecturerInfolist,courseInfoArray)
        _binding.recyclerViewChooseLecturer.layoutManager = layoutManager
        _binding.recyclerViewChooseLecturer.adapter = adapter
        activity?.let {
            adapter.lecturerAvailable(it)
        }

    }

    private fun getLecturerInfo(){
        try {
            val cursor = database.rawQuery("SELECT u.user_TRid, u.user_name, u.user_surname FROM Tbl_User as u JOIN Tbl_Login as l ON u.loginId = l.ID WHERE u.user_title = ? and l.approval = ? ", arrayOf("Lecturer","true"))
            val lecturerIdIndx = cursor.getColumnIndex("user_TRid")
            val lectuerNameIndx = cursor.getColumnIndex("user_name")
            val lectuerSurnameIndx = cursor.getColumnIndex("user_surname")

            while (cursor.moveToNext()){
                val lecturerInfoObject = LecturerInfo(
                    cursor.getString(lecturerIdIndx),
                    cursor.getString(lectuerNameIndx),
                    cursor.getString(lectuerSurnameIndx))

                lecturerInfolist.add(lecturerInfoObject)
            }

            cursor.close()
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }
}