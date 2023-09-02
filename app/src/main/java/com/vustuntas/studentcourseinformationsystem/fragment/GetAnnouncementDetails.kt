package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentGetAnnouncementDetailsBinding

class GetAnnouncementDetails : Fragment() {
    private lateinit var _binding : FragmentGetAnnouncementDetailsBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private var announcementId : Int? = null
    private var announcementTitle : String? = null
    private var announcementContent :String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetAnnouncementDetailsBinding.inflate(inflater,container,false)
        activity?.let {
            database = it.openOrCreateDatabase("SCIS", Context.MODE_PRIVATE,null)
        }
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            announcementId = GetAnnouncementDetailsArgs.fromBundle(it).announcementId
        }
        getAnnouncementDetails()
        _binding.announcementTitle.setText(announcementTitle)
        _binding.announcementContent.setText(announcementContent)
    }

    private fun getAnnouncementDetails(){
        try {
            val cursor = database.rawQuery("select * from Tbl_Announcement where ID = ?", arrayOf(announcementId.toString()))
            val announcementTitleIndx = cursor.getColumnIndex("announcement_title")
            val announcementContentIndx = cursor.getColumnIndex("announcement_content")
            while (cursor.moveToNext()){
                announcementTitle = cursor.getString(announcementTitleIndx)
                announcementContent = cursor.getString(announcementContentIndx)
                println(cursor.getString(announcementContentIndx))
            }
            cursor.close()
        }
        catch (e:Exception){
            e.printStackTrace()
            println(e.localizedMessage)
        }
    }
}