package com.vustuntas.studentcourseinformationsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.CourseInfo
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R

class GetMyCoursesAdapter (val arrayListCourses : ArrayList<CourseInfo>, view:View) : RecyclerView.Adapter<GetMyCoursesAdapter.GetMyCoursesVH>() {
    class GetMyCoursesVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetMyCoursesVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_get_my_courses,parent,false)
        return GetMyCoursesVH(itemView)
    }

    override fun onBindViewHolder(holder: GetMyCoursesVH, position: Int) {
        val courseInfo = arrayListCourses.get(position) as CourseInfo
        holder.itemView.findViewById<TextView>(R.id.courseCodeTextView).text = courseInfo.courseCode
        holder.itemView.findViewById<TextView>(R.id.courseNameTextView).text = courseInfo.courseName
        //holder.itemView.findViewById<TextView>(R.id.courseLecturerIdTextView).text = courseInfo.courseLecturer
        holder.itemView.findViewById<TextView>(R.id.courseCreditTextView).text = "${courseInfo.courseCredit} ECTS"

    }

    override fun getItemCount(): Int {
        return arrayListCourses.size
    }
}