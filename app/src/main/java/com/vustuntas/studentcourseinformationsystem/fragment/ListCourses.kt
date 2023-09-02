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
import com.vustuntas.studentcourseinformationsystem.adapter.ListCourseAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentListCoursesBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.CourseInfo
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.util.ArrayList

class ListCourses : Fragment() {
    private lateinit var _binding : FragmentListCoursesBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private lateinit var courseArrayList : ArrayList<CourseInfo>
    private lateinit var adapter : ListCourseAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListCoursesBinding.inflate(inflater,container,false )
        courseArrayList = ArrayList<CourseInfo>()
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
            adapter = ListCourseAdapter(courseArrayList,it)
        }
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllCourse()
        val linearLayout = LinearLayoutManager(context)
        _binding.recyclerViewListCourses.layoutManager = linearLayout
        _binding.recyclerViewListCourses.adapter = adapter
    }

    private fun getAllCourse(){
        try {
            val cursor = database.rawQuery("SELECT C.ID,C.course_code,C.course_name,U.user_name,U.user_surname,C.course_credit FROM Tbl_Courses as C JOIN Tbl_User as U ON C.course_lecturerId = U.user_TRid",null)
            val courseIdIndx = cursor.getColumnIndex("ID")
            val courseCodeIndx = cursor.getColumnIndex("course_code")
            val courseNameIndx = cursor.getColumnIndex("course_name")
            val courseLecturerNameIndx = cursor.getColumnIndex("user_name")
            val courseLecturerSurnameIndx = cursor.getColumnIndex("user_surname")
            val courseCreditIndx = cursor.getColumnIndex("course_credit")

            while (cursor.moveToNext()){
                val lecturerNameSurname = "${cursor.getString(courseLecturerNameIndx)} ${cursor.getString(courseLecturerSurnameIndx)}"
                val courseInfoObject = CourseInfo(
                    cursor.getString(courseIdIndx),
                    cursor.getString(courseCodeIndx),
                    cursor.getString(courseNameIndx),
                    lecturerNameSurname,
                    cursor.getString(courseCreditIndx)
                )
                courseArrayList.add(courseInfoObject)
            }
            cursor.close()
        }
        catch (E :Exception){
            println(E.localizedMessage)
        }
    }
}