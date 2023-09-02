package com.vustuntas.studentcourseinformationsystem.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.navArgument
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.fragment.ChooseLecturer
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.ChooseLecturerDirections
import com.vustuntas.studentcourseinformationsystem.helperClass.ChooseLectureDestination
import com.vustuntas.studentcourseinformationsystem.helperClass.LecturerInfo
import java.util.ArrayList

class ChooseLecturerAdapter(var lecturerList : ArrayList<LecturerInfo>,var courseInfoArray :Array<String>) : RecyclerView.Adapter<ChooseLecturerAdapter.ChooseLecturerVH>() {
    private lateinit var singletonClass : ChooseLectureDestination.dest
    class ChooseLecturerVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseLecturerVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_choose_lecturer,parent,false)
        return ChooseLecturerVH(itemView)
    }

    override fun onBindViewHolder(holder: ChooseLecturerVH, position: Int) {
        singletonClass = ChooseLectureDestination.dest
        val lecturerInfoObject = lecturerList.get(position) as LecturerInfo
        holder.itemView.findViewById<TextView>(R.id.lecturerIdTextView_recycler_row_choose_lecturer).text = "Lecturer Id: ${lecturerInfoObject.lecturerId}"
        holder.itemView.findViewById<TextView>(R.id.lecturerNameTextView_recycler_row_choose_lecturer).text = "Lecturer Name: ${lecturerInfoObject.lecturerName}"
        holder.itemView.findViewById<TextView>(R.id.lecturerSurnameTextView_recycler_row_choose_lecturer).text = "Lecturer Surname: ${lecturerInfoObject.lecturerSurname}"
        holder.itemView.setOnClickListener {
            val action = ChooseLecturerDirections.actionChooseLecturerToNavAddCourse(lecturerInfoObject.lecturerId,courseInfoArray)
            val destination = "cameFromChooseAdapter"
            singletonClass.destination = destination
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return lecturerList.size
    }

    fun lecturerAvailable(activity: Activity){
        val userCount = getItemCount()
        if(userCount == 0){
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Number of Lecturers")
            alertDialog.setMessage("If you don't see any lecturers, confirm the lecturers registered in your system but not confirmed!")
            alertDialog.setPositiveButton("Confirmation Screen",DialogInterface.OnClickListener { dialogInterface, i ->
                val action = ChooseLecturerDirections.actionChooseLecturerToNavUserConfirm()
                Navigation.findNavController(activity,R.id.nav_host_fragment_content_admin_navigation_drawer).navigate(action)
            })
            alertDialog.setNeutralButton("Okey",DialogInterface.OnClickListener { dialogInterface, i ->

            }).show()

        }
    }
}