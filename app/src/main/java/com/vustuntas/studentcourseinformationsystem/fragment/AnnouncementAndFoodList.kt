package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.adapter.GetAnnouncementAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAnnouncementAndFoodListBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.AnnouncementInfo
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class AnnouncementAndFoodList : Fragment() {
    private lateinit var _binding : FragmentAnnouncementAndFoodListBinding
    private val binding get() = _binding.root
    private lateinit var database :SQLiteDatabase
    private lateinit var announcementsArrayList : ArrayList<AnnouncementInfo>
    private lateinit var adapter : GetAnnouncementAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnnouncementAndFoodListBinding.inflate(inflater,container,false)
        activity?.let { database = it.openOrCreateDatabase("SCIS", Context.MODE_PRIVATE,null) }
        announcementsArrayList = ArrayList<AnnouncementInfo>()
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAnnouncement()
        val layoutManger = LinearLayoutManager(context)
        activity?.let {
            adapter = GetAnnouncementAdapter(announcementsArrayList,_binding.root)
        }
        _binding.recyclerViewAnnouncement.layoutManager = layoutManger
        _binding.recyclerViewAnnouncement.adapter = adapter

        mealOfTheDay()
    }

    private fun getAnnouncement(){
        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_Announcement",null)
            val announcementIdIndx = cursor.getColumnIndex("ID")
            val announcementTitleIndx = cursor.getColumnIndex("announcement_title")
            while (cursor.moveToNext()){
                val announcementInfoObject = AnnouncementInfo(cursor.getInt(announcementIdIndx),cursor.getString(announcementTitleIndx))
                announcementsArrayList.add(announcementInfoObject)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun mealOfTheDay(){
        val currentTimeStamp = System.currentTimeMillis()
        val getCurrentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = getCurrentDate.format(Date(currentTimeStamp))

        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_Foods WHERE food_menuDate = ? ORDER BY ID DESC LIMIT 1", arrayOf(formattedDate))
            val menuDateIndx = cursor.getColumnIndex("food_menuDate")
            val soupNameIndx = cursor.getColumnIndex("food_soupName")
            val mainDishIndx = cursor.getColumnIndex("food_mainFood")
            while (cursor.moveToNext()){
                _binding.menuDateTextView.text = cursor.getString(menuDateIndx)
                _binding.soupNameTextView.text = cursor.getString(soupNameIndx)
                _binding.mainFoodTextView.text = cursor.getString(mainDishIndx)
            }
            cursor.close()
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }

    }
}