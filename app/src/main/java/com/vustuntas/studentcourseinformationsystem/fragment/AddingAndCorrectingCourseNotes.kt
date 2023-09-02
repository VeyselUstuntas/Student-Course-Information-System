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
import com.vustuntas.studentcourseinformationsystem.adapter.AddingAndCorrectingCourseAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAddingAndCorrectingCourseNotesBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.CourseInfo
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.util.ArrayList

class AddingAndCorrectingCourseNotes : Fragment() {
    private lateinit var _binding : FragmentAddingAndCorrectingCourseNotesBinding
    private val binding get() = _binding.root
    private lateinit var database:SQLiteDatabase
    private var singletonClassUserInfo = PutUserInfo.userInfo
    private var lecturerId : String? = null
    private lateinit var arrayListCourse : ArrayList<CourseInfo>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddingAndCorrectingCourseNotesBinding.inflate(inflater,container,false)
        lecturerId = singletonClassUserInfo.userId
        arrayListCourse = ArrayList<CourseInfo>()
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCourses()

        _binding.recyclerView.layoutManager = LinearLayoutManager(context)
        _binding.recyclerView.adapter = AddingAndCorrectingCourseAdapter(arrayListCourse,_binding.root)
    }

    private fun getCourses(){
        try {
            val cursor = database.rawQuery("SELECT c.ID, c.course_code, c.course_name, u.user_name, u.user_surname,c.course_credit FROM Tbl_Courses as c JOIN Tbl_User as u ON c.course_lecturerId = u.user_TRid WHERE course_lecturerId = ?", arrayOf(lecturerId))
            val courseIdIndx = cursor.getColumnIndex("ID")
            val courseCodeIndx = cursor.getColumnIndex("course_code")
            val courseNameIndx = cursor.getColumnIndex("course_name")
            val courseLecturerNameIndx = cursor.getColumnIndex("user_name")
            val courseLecturerSurnameIndx = cursor.getColumnIndex("user_surname")
            val courseCreditIndx = cursor.getColumnIndex("course_credit")
            while(cursor.moveToNext()){
                var lecturerNameSurname : String = "${cursor.getString(courseLecturerNameIndx)} ${cursor.getString(courseLecturerSurnameIndx)}"
                val courseInfoObject = CourseInfo(
                    cursor.getString(courseIdIndx),
                    cursor.getString(courseCodeIndx),
                    cursor.getString(courseNameIndx),
                    lecturerNameSurname,
                    cursor.getString(courseCreditIndx)
                )
                arrayListCourse.add(courseInfoObject)
            }

            cursor.close()
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }

    }
}