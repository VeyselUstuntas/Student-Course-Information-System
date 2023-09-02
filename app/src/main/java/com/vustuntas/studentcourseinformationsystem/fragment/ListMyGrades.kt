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
import com.vustuntas.studentcourseinformationsystem.adapter.ListMyGradesAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentListMyGradesBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.GradesInfo
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.util.ArrayList
import kotlin.math.sin

class ListMyGrades : Fragment() {
    private lateinit var _binding : FragmentListMyGradesBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private var singletonUserInfo = PutUserInfo.userInfo
    private var studentId :String? = null
    private lateinit var gradeArrayList : ArrayList<GradesInfo>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListMyGradesBinding.inflate(inflater,container,false)
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        studentId = singletonUserInfo.userId
        gradeArrayList = ArrayList<GradesInfo>()
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMyGrades()
        _binding.recyclerView.layoutManager = LinearLayoutManager(context)
        _binding.recyclerView.adapter = ListMyGradesAdapter(gradeArrayList)
    }

    private fun getMyGrades(){
        try {
            gradeArrayList.clear()
            val cursor = database.rawQuery("select c.course_name,sn.midterm_grade,sn.final_grade,sn.letter_grade from Tbl_SelectedCourses as sc LEFt JOIN Tbl_StudentsNote as sn ON sn.selectedCourseId = sc.ID JOIN Tbl_Courses as c On c.ID = sc.course_id where sc.student_id = ?", arrayOf(studentId))
            val courseNameIndx = cursor.getColumnIndex("c.course_name")
            val midtermNoteIndx = cursor.getColumnIndex("sn.midterm_grade")
            val finalNoteIndx = cursor.getColumnIndex("sn.final_grade")
            val letterNoteIndx = cursor.getColumnIndex("sn.letter_grade")
            while (cursor.moveToNext()){
                val courseName = cursor.getString(courseNameIndx) ?: null
                val midtermNote = cursor.getString(midtermNoteIndx)?: null
                val finalNote  =cursor.getString(finalNoteIndx)?: null
                val letterNote = cursor.getString(letterNoteIndx)?: null

                val gradeInfoObject = GradesInfo(courseName, midtermNote, finalNote, letterNote)
                gradeArrayList.add(gradeInfoObject)
            }
            cursor.close()
        }
        catch (E :Exception){
            println(E.localizedMessage)
        }
    }

}