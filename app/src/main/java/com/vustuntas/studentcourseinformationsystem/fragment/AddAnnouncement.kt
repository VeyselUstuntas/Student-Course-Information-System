package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.adapter.ListAnnouncementsAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAddAnnouncementBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.AnnouncementInfo
import java.util.ArrayList

class AddAnnouncement : Fragment() {
    private lateinit var _binding : FragmentAddAnnouncementBinding
    private val binding get() = _binding.root

    private lateinit var database: SQLiteDatabase
    private var announcementTitle : String = ""
    private var announcementContent : String = ""
    private lateinit var announcementInfoArray : ArrayList<AnnouncementInfo>
    private lateinit var adapter : ListAnnouncementsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAnnouncementBinding.inflate(inflater,container,false)
        _binding.saveAnnouncement.setOnClickListener { saveAnnouncement(it) }
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        announcementInfoArray = ArrayList<AnnouncementInfo>()
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        activity?.let {
            adapter = ListAnnouncementsAdapter(announcementInfoArray,it)
        }
        _binding.recyclerViewAddAnnouncement.layoutManager = layoutManager
        _binding.recyclerViewAddAnnouncement.adapter = adapter
        getAnnouncement()
    }


    fun saveAnnouncement(view:View){
        announcementTitle = _binding.announcementTitle.text.toString()
        announcementContent = _binding.announcementContent.text.toString()

        if(!announcementContent.equals("") && !announcementTitle.equals("")){
            try {
                val insertAnnouncementQuery = "INSERT INTO Tbl_Announcement (announcement_title,announcement_content) VALUES (?,?)"
                val statment = database.compileStatement(insertAnnouncementQuery)
                statment.bindString(1,announcementTitle)
                statment.bindString(2,announcementContent)
                statment.execute()
                _binding.announcementTitle.text.clear()
                _binding.announcementContent.text.clear()
                announcementInfoArray.clear()
                getAnnouncement()
            }
            catch (E : Exception){
                E.printStackTrace()
            }
        }
        else
            Toast.makeText(context,"Please Enter Announcement Info",Toast.LENGTH_SHORT).show()

    }

    private fun getAnnouncement(){
        try {
            val cursor = database.rawQuery("select * from Tbl_Announcement",null)
            val announcementIdIndx = cursor.getColumnIndex("ID")
            val announcementTitleIndx = cursor.getColumnIndex("announcement_title")
            while (cursor.moveToNext()){
                val announcementInfoObject = AnnouncementInfo(cursor.getInt(announcementIdIndx),cursor.getString(announcementTitleIndx))
                announcementInfoArray.add(announcementInfoObject)
            }
            adapter.notifyDataSetChanged()
            cursor.close()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

}