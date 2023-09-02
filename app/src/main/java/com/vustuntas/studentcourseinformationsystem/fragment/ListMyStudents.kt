package com.vustuntas.studentcourseinformationsystem.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.adapter.GetMyStudentsAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentListMyStudentsBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import com.vustuntas.studentcourseinformationsystem.helperClass.StudentInfo
import java.lang.Exception
import java.util.ArrayList

class ListMyStudents : Fragment() {
    private lateinit var _binding : FragmentListMyStudentsBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private val singletonClassUserInfo = PutUserInfo.userInfo
    private var lecturerId : String? = null
    private var courseId : String? = null
    private var courseName : String? = null
    private lateinit var studentInfoArrayList : ArrayList<StudentInfo>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListMyStudentsBinding.inflate(inflater,container,false)
        studentInfoArrayList = ArrayList<StudentInfo>()
        activity?.let { database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null) }
        lecturerId = singletonClassUserInfo.userId
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            courseId = ListMyStudentsArgs.fromBundle(it).courseId
        }
        getCourseName()
        getStudentsTakingCourse()
        studentInfoArrayList.forEach {
            println(it.studentName)
        }
        _binding.courseName.text = courseName.toString()
        val adapter = GetMyStudentsAdapter(studentInfoArrayList,_binding.root,courseId.toString())
        if(adapter.itemCount == 0){
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("No Students to View!")
            alertDialog.setMessage("You cannot see a student on this screen because there is no student taking your course.")
            alertDialog.setNeutralButton("Okey",DialogInterface.OnClickListener { dialogInterface, i ->

            }).show()
        }
        else{
            val layoutManager = LinearLayoutManager(context)
            _binding.recyclerView.layoutManager = layoutManager
            _binding.recyclerView.adapter = adapter
        }
    }

    private fun getStudentsTakingCourse(){
        try {
            val cursor = database.rawQuery("SELECT u.user_TRid, u.user_name, u.user_surname, u.user_mailAddress, u.user_image FROM Tbl_SelectedCourses AS sc " +
                    "JOIN Tbl_Courses AS c ON sc.course_id = c.ID JOIN Tbl_User AS u ON sc.student_id = u.user_TRid WHERE c.course_lecturerId = ? and sc.course_id = ? ", arrayOf(lecturerId,courseId))
            val studentIdndx = cursor.getColumnIndex("u.user_TRid")
            val studentNameIndx = cursor.getColumnIndex("u.user_name")
            val studentSurnameIndx = cursor.getColumnIndex("u.user_surname")
            val studentMailAddressIndx = cursor.getColumnIndex("u.user_mailAddress")
            val studentImageIndx = cursor.getColumnIndex("u.user_image")
            while (cursor.moveToNext()){
                val studentInfoObject = StudentInfo(
                    cursor.getString(studentIdndx),
                    cursor.getString(studentNameIndx),
                    cursor.getString(studentSurnameIndx),
                    cursor.getString(studentMailAddressIndx),
                    cursor.getBlob(studentImageIndx))
                studentInfoArrayList.add(studentInfoObject)
            }
            cursor.close()
        }
        catch (E : Exception){
            println(E.localizedMessage)
        }
    }

    private fun getCourseName(){
        try {
            val cursor = database.rawQuery("SELECT course_name FROM Tbl_Courses WHERE ID = ?", arrayOf(courseId))
            val courseNameIndx = cursor.getColumnIndex("course_name")
            while (cursor.moveToNext()){
                courseName = cursor.getString(courseNameIndx)
            }
            cursor.close()
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }
    }

}