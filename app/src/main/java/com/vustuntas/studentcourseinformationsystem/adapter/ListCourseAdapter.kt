package com.vustuntas.studentcourseinformationsystem.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.CourseInfo
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo

class ListCourseAdapter(val courseList : ArrayList<CourseInfo>,val activity:Activity) : RecyclerView.Adapter<ListCourseAdapter.CourseListVH>() {
    private val singletonUserInfo = PutUserInfo.userInfo
    private lateinit var database: SQLiteDatabase
    class CourseListVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_list_courses,parent,false)
        return CourseListVH(itemView)
    }

    override fun onBindViewHolder(holder: CourseListVH, position: Int) {
        database = holder.itemView.context.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        val courseInfoObject = courseList.get(position) as CourseInfo
        holder.itemView.findViewById<TextView>(R.id.recycler_row_courseCodeTextView).text = courseInfoObject.courseCode
        holder.itemView.findViewById<TextView>(R.id.recycler_row_courseNameTextView).text = courseInfoObject.courseName
        holder.itemView.findViewById<TextView>(R.id.recycler_row_courseLecturerTextView).text = courseInfoObject.courseLecturer
        holder.itemView.findViewById<TextView>(R.id.recycler_row_courseCreditTextView).text = "${courseInfoObject.courseCredit} ECTS"

        holder.itemView.findViewById<Button>(R.id.recycler_row_course_takeDropButton).setOnClickListener {
            val studentId = singletonUserInfo.userId
            if(studentId != null){
                val courseAvailable = courseAvailable(studentId,courseInfoObject.courseId,courseInfoObject)
                if(!courseAvailable){
                    val addedCourseQuery = "INSERT INTO Tbl_SelectedCourses (student_id,course_id,course_code) VALUES (?,?,?)"
                    val statment = database.compileStatement(addedCourseQuery)
                    statment.bindString(1,studentId)
                    statment.bindString(2,courseInfoObject.courseId.toString())
                    statment.bindString(3,courseInfoObject.courseCode)
                    statment.execute()
                    Toast.makeText(holder.itemView.context,"Selected Course Added to My Courses",Toast.LENGTH_LONG).show()
                }
                else{
                    val alertDialog = AlertDialog.Builder(holder.itemView.context)
                    alertDialog.setTitle("Dropping Added Course")
                    alertDialog.setMessage("Do You Want to Drop the Course You Are Taking?")
                    alertDialog.setNegativeButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

                        var selectedCourseID : String? = null
                        try {
                            val cursor = database.rawQuery("select ID from Tbl_SelectedCourses WHERE student_id = ? and course_id = ?", arrayOf(studentId,courseInfoObject.courseId))
                            val selectedCourseIDIndx = cursor.getColumnIndex("ID")
                            while (cursor.moveToNext()){
                                selectedCourseID = cursor.getString(selectedCourseIDIndx)
                            }
                            cursor.close()
                            println("selectedCourseId $selectedCourseID")
                        }
                        catch (e:Exception){
                            println(e.localizedMessage)
                        }

                        val deleteCourseQuery = "DELETE FROM Tbl_SelectedCourses WHERE student_id = ? and course_id = ?"
                        var statment = database.compileStatement(deleteCourseQuery)
                        statment.bindString(1,studentId)
                        statment.bindString(2,courseInfoObject.courseId.toString())
                        statment.execute()


                        val deleteSelectedCourseNote = "DELETE FROM Tbl_StudentsNote WHERE studentId = ? and selectedCourseId = ?"
                        val statment2 = database.compileStatement(deleteSelectedCourseNote)
                        statment2.bindString(1,studentId)
                        statment2.bindString(2,selectedCourseID)
                        statment2.execute()
                        Toast.makeText(holder.itemView.context,"Course Drop Successful",Toast.LENGTH_LONG).show()

                    })
                    alertDialog.setPositiveButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(holder.itemView.context,"Course Drop Failed",Toast.LENGTH_LONG).show()
                    }).show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    private fun courseAvailable(studentId :String, courseId : String,courseObject : CourseInfo) : Boolean{
        try {
            var cursor = database.rawQuery("SELECT * FROM Tbl_SelectedCourses WHERE student_id = ? and course_id = ?", arrayOf(studentId,courseId.toString()))
            val studentIdIndx = cursor.getColumnIndex("student_id")
            val courseIdIndx = cursor.getColumnIndex("course_id")
            while (cursor.moveToNext()){
                if(courseObject.courseId.equals(cursor.getInt(courseIdIndx).toString()) && studentId.equals(cursor.getString(studentIdIndx))){
                    return true
                }
            }
            cursor.close()
            return false
        }
        catch (e:Exception){
            println(e.localizedMessage)
            return true
        }
    }
}