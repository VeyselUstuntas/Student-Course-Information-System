package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentUpdateAndDeleteAnnouncementBinding

class UpdateAndDeleteAnnouncement : Fragment() {
    private lateinit var _binding: FragmentUpdateAndDeleteAnnouncementBinding
    private val binding get() = _binding.root
    private lateinit var database : SQLiteDatabase
    private var incomingId : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateAndDeleteAnnouncementBinding.inflate(inflater,container,false)
        _binding.deleteAnnouncement.setOnClickListener { deleteAnnouncement(it) }
        _binding.updateAnnouncement.setOnClickListener { updateAnnouncement(it) }
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_Announcement(ID INTEGER PRIMARY KEY, announcement_title VARCHAR, announcement_content VARCHAR)")
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            incomingId = UpdateAndDeleteAnnouncementArgs.fromBundle(it).announcementId
        }
        getAnnouncementInfo()

    }

    fun updateAnnouncement(view:View){
        val updateAnnouncementQuery = "UPDATE Tbl_Announcement SET announcement_title = ? , announcement_content = ? WHERE ID = ?"
        val statment = database.compileStatement(updateAnnouncementQuery)
        statment.bindString(1,_binding.announcementTitle.text.toString())
        statment.bindString(2,_binding.announcementContent.text.toString())
        statment.bindString(3,incomingId.toString())
        statment.execute()
        Toast.makeText(context,"Selected Announcement Updated",Toast.LENGTH_SHORT).show()
        val action = UpdateAndDeleteAnnouncementDirections.actionUpdateAndDeleteAnnouncementToNavAddAnnouncement()
        Navigation.findNavController(view).navigate(action)
    }

    fun deleteAnnouncement(view:View){
        val deleteAnnouncementQuery = "DELETE FROM Tbl_Announcement WHERE ID = ?"
        val statment = database.compileStatement(deleteAnnouncementQuery)
        statment.bindString(1,incomingId.toString())
        statment.execute()
        Toast.makeText(context,"Selected Announcement Deleted",Toast.LENGTH_SHORT).show()
        val action = UpdateAndDeleteAnnouncementDirections.actionUpdateAndDeleteAnnouncementToNavAddAnnouncement()
        Navigation.findNavController(view).navigate(action)
    }

    private fun getAnnouncementInfo(){
        try {
            if(incomingId != null){
                val cursor = database.rawQuery("SELECT * FROM Tbl_Announcement WHERE ID = ?", arrayOf(incomingId.toString()))
                val announcementTitleIndx = cursor.getColumnIndex("announcement_title")
                val announcementContentIndx = cursor.getColumnIndex("announcement_content")
                while (cursor.moveToNext()){
                   _binding.announcementTitle.setText(cursor.getString(announcementTitleIndx))
                   _binding.announcementContent.setText(cursor.getString(announcementContentIndx))
                }
                cursor.close()
            }
        }
        catch (e : Exception){

        }
    }

}