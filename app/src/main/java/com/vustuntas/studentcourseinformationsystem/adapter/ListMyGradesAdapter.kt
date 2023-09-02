package com.vustuntas.studentcourseinformationsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.helperClass.GradesInfo
import java.util.ArrayList

class ListMyGradesAdapter(val gradeArrayList : ArrayList<GradesInfo>) : RecyclerView.Adapter<ListMyGradesAdapter.ListGradesVH>() {
    class ListGradesVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListGradesVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_list_my_grades,parent,false)
        return ListGradesVH(itemView)
    }

    override fun onBindViewHolder(holder: ListGradesVH, position: Int) {
        val gradeObject = gradeArrayList.get(position) as GradesInfo

        holder.itemView.findViewById<TextView>(R.id.recycler_row_list_my_grades_courseNameTextView).text = gradeObject.courseName ?: "-"
        holder.itemView.findViewById<TextView>(R.id.recycler_row_list_my_grades_midtermGradeTextView).text = gradeObject.midtermNote ?: "-"
        holder.itemView.findViewById<TextView>(R.id.recycler_row_list_my_grades_finalGradeTextView).text = gradeObject.finalNote ?: "-"
        holder.itemView.findViewById<TextView>(R.id.recycler_row_list_my_grades_letterGradeTextView).text = gradeObject.letterNote ?: "-"

    }

    override fun getItemCount(): Int {
        return gradeArrayList.size
    }
}