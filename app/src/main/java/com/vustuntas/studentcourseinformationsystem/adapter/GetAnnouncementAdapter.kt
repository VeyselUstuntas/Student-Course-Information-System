package com.vustuntas.studentcourseinformationsystem.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.AnnouncementInfo
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AnnouncementAndFoodList
import com.vustuntas.studentcourseinformationsystem.fragment.AnnouncementAndFoodListDirections
import com.vustuntas.studentcourseinformationsystem.fragment.UpdateAndDeleteFoodListDirections

class GetAnnouncementAdapter(var announcementArray:ArrayList<AnnouncementInfo>,val view : View) : RecyclerView.Adapter<GetAnnouncementAdapter.GetAnnouncementVH>() {
    class GetAnnouncementVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetAnnouncementVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_get_announcement,parent,false)
        return GetAnnouncementVH(itemView)
    }

    override fun onBindViewHolder(holder: GetAnnouncementVH, position: Int) {
        val announcementInfoObject = announcementArray.get(position) as AnnouncementInfo
        holder.itemView.findViewById<TextView>(R.id.recycler_row_get_announcement_Title).text = announcementInfoObject.announcementTitle
        val announcementId = announcementInfoObject.announcementId
        holder.itemView.setOnClickListener {
            val action = AnnouncementAndFoodListDirections.actionNavAnnouncementAndFoodListToGetAnnouncementDetails(announcementId)
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return announcementArray.size
    }
}