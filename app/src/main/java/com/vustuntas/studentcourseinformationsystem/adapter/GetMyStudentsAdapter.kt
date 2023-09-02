package com.vustuntas.studentcourseinformationsystem.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.StudentInfo
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AddingAndCorrectingCourseNotesDirections
import com.vustuntas.studentcourseinformationsystem.fragment.ListMyStudentsDirections
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo

class GetMyStudentsAdapter(val studentArrayList : ArrayList<StudentInfo>, val view:View,val courseId : String) : RecyclerView.Adapter<GetMyStudentsAdapter.MyStudentsVH>() {
    class MyStudentsVH(itemView : View) :RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudentsVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_my_students_list,parent,false)
        return MyStudentsVH(itemView)
    }
    private val singletonUserInfo = PutUserInfo.userInfo
    override fun onBindViewHolder(holder: MyStudentsVH, position: Int) {
        val studentInfoObject = studentArrayList.get(position) as StudentInfo
        val studentNameSurname = "${studentInfoObject.studentName} ${studentInfoObject.studentSurname}"
        holder.itemView.findViewById<TextView>(R.id.studentIdTextView).text = studentInfoObject.studentId
        holder.itemView.findViewById<TextView>(R.id.studentNameSurnameTextView).text = studentNameSurname
        holder.itemView.findViewById<TextView>(R.id.studentMailAddressTextView).text = studentInfoObject.studentMailAddress
        studentInfoObject.studentImage?.let {
            val studentImage = BitmapFactory.decodeByteArray(it,0,it.size)
            holder.itemView.findViewById<ImageView>(R.id.studentImageView).setImageBitmap(studentImage)
        }
        holder.itemView.setOnClickListener {
            val studentId = studentInfoObject.studentId
            val lecturerId = singletonUserInfo.userId
            val courseId = courseId
            val action = ListMyStudentsDirections.actionListMyStudentsToAddOrEditStudentGrade(studentId,lecturerId!!,courseId)
            Navigation.findNavController(view).navigate(action)

        }

    }

    override fun getItemCount(): Int {
        return studentArrayList.size
    }
}