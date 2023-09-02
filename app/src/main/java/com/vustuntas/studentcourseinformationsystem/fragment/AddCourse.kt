package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAddCourseBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.ChooseLectureDestination
import java.lang.reflect.Array
import java.util.ArrayList
import kotlin.math.sin

class AddCourse : Fragment() {
    private lateinit var _binding : FragmentAddCourseBinding
    private val binding get() = _binding.root
    private lateinit var database: SQLiteDatabase
    private lateinit var singletonClass : ChooseLectureDestination.dest
    private var courseLecturer : String? = null
    private var courseCode = ""
    private var courseName = ""
    private var courseCredit = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCourseBinding.inflate(inflater,container,false)
        _binding.addCourseButton.setOnClickListener { addCourse(it) }
        _binding.courseLecturerButton.setOnClickListener { chooseLecturer(it) }
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        singletonClass = ChooseLectureDestination.dest
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val destination = singletonClass.destination
        if(destination.equals("cameFromChooseAdapter")){
            arguments?.let {
                courseLecturer = AddCourseArgs.fromBundle(it).lecturerId
                val array = AddCourseArgs.fromBundle(it).courseInfoArray
                courseCode = array.get(0)
                courseName = array.get(1)
                courseCredit = array.get(2)
            }
            try {
                val cursor = database.rawQuery("select * from Tbl_User where user_TRid = ?", arrayOf(courseLecturer))
                val lecturerNameIndx = cursor.getColumnIndex("user_name")
                val lecturerSurnameIndx = cursor.getColumnIndex("user_surname")
                while (cursor.moveToNext()){
                    _binding.courseLecturerEditText.setText("${cursor.getString(lecturerNameIndx)} ${cursor.getString(lecturerSurnameIndx)}")
                }

                cursor.close()
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            _binding.courseCreditEditText.setText(courseCredit)
            _binding.courseNameEditText.setText(courseName)
            _binding.courseCodeEditText.setText(courseCode)
        }
    }

    private fun chooseLecturer(view : View){
        courseCode = _binding.courseCodeEditText.text.toString()
        courseName = _binding.courseNameEditText.text.toString()
        courseCredit = _binding.courseCreditEditText.text.toString()
        if(!courseCode.equals("") && !courseName.equals("") && !courseCredit.equals("")){
            var courseInfoArray = arrayOf(courseCode,courseName,courseCredit)
            val action = AddCourseDirections.actionNavAddCourseToChooseLecturer(courseInfoArray)
            Navigation.findNavController(view).navigate(action)
        }
        else{
            Toast.makeText(context,"first enter the course information",Toast.LENGTH_SHORT).show()
        }
    }


    private fun addCourse(view : View){
        try {
            courseCode = _binding.courseCodeEditText.text.toString()
            courseName = _binding.courseNameEditText.text.toString()
            courseCredit = _binding.courseCreditEditText.text.toString()
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_Courses (ID INTEGER PRIMARY KEY, course_code VARCHAR, course_name VARCHAR, course_lecturerId VARCHAR, course_credit VARCHAR )")
            if(!courseCode.equals("") && !courseName.equals("") && !courseCredit.equals("")){
                val courseControlCursor = database.rawQuery("SELECT * FROM Tbl_Courses WHERE course_code = ? ", arrayOf(courseCode))
                val courseCodeControlIndx = courseControlCursor.getColumnIndex("course_code")
                var availableCourse : Boolean = false
                while (courseControlCursor.moveToNext()){
                    if(courseControlCursor.getString(courseCodeControlIndx) == courseCode){
                        availableCourse = true
                        break
                    }
                }

                if(!availableCourse){
                    val addCourseQuery = "INSERT INTO Tbl_Courses (course_code,course_name,course_lecturerId,course_credit) VALUES(?, ?, ?, ?)"
                    val statment = database.compileStatement(addCourseQuery)
                    statment.bindString(1,courseCode)
                    statment.bindString(2,courseName)
                    statment.bindString(3,courseLecturer)
                    statment.bindString(4,courseCredit)
                    statment.execute()
                    Toast.makeText(context,"Course addition process successful",Toast.LENGTH_SHORT).show()
                    clearEditText()
                }
                else{
                    Toast.makeText(context,"The Course you are trying to add is already available",Toast.LENGTH_SHORT).show()
                }
            }
            else
                Toast.makeText(context,"Please Enter Complete Course Information",Toast.LENGTH_SHORT).show()
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun clearEditText(){
        _binding.courseLecturerEditText.text.clear()
        _binding.courseCodeEditText.text.clear()
        _binding.courseNameEditText.text.clear()
        _binding.courseCreditEditText.text.clear()
    }
}