package com.vustuntas.studentcourseinformationsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AddingAndCorrectingCourseNotes
import com.vustuntas.studentcourseinformationsystem.fragment.AddingAndCorrectingCourseNotesDirections
import com.vustuntas.studentcourseinformationsystem.helperClass.CourseInfo

class AddingAndCorrectingCourseAdapter(val arrayList : ArrayList<CourseInfo>, val view:View) : RecyclerView.Adapter<AddingAndCorrectingCourseAdapter.AddingAndCorrectingCourseNotesVH>() {
    class AddingAndCorrectingCourseNotesVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddingAndCorrectingCourseNotesVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_adding_and_correcting_course_note,parent,false)
        return AddingAndCorrectingCourseNotesVH(itemView)
    }

    override fun onBindViewHolder(holder: AddingAndCorrectingCourseNotesVH, position: Int) {
        val courseInfoObject = arrayList.get(position) as CourseInfo
        holder.itemView.findViewById<TextView>(R.id.recycler_row_addingAndCorrectingCourseNotes).text = courseInfoObject.courseName
        holder.itemView.setOnClickListener {
            val courseId = courseInfoObject.courseId
            val action = AddingAndCorrectingCourseNotesDirections.actionNavEditStudentNotesToListMyStudents(courseId)
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}