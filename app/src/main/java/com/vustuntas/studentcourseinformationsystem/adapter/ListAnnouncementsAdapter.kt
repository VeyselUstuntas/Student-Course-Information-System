package com.vustuntas.studentcourseinformationsystem.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AddAnnouncementDirections
import com.vustuntas.studentcourseinformationsystem.helperClass.AnnouncementInfo
import java.util.ArrayList

class ListAnnouncementsAdapter(val arrayListAnnouncement : ArrayList<AnnouncementInfo>,val activity: Activity) : RecyclerView.Adapter<ListAnnouncementsAdapter.ListAnnouncementVH>() {
    class ListAnnouncementVH (itemView : View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAnnouncementVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_add_announcement,parent,false)
        return ListAnnouncementVH(itemView)
    }

    override fun onBindViewHolder(holder: ListAnnouncementVH, position: Int) {
        val announcementInfoObject = arrayListAnnouncement.get(position) as AnnouncementInfo
        holder.itemView.findViewById<TextView>(R.id.recycler_row_add_announcement_announcementTitle).text = announcementInfoObject.announcementTitle
        holder.itemView.setOnClickListener{
            val action = AddAnnouncementDirections.actionNavAddAnnouncementToUpdateAndDeleteAnnouncement(announcementInfoObject.announcementId)
            Navigation.findNavController(activity,R.id.nav_host_fragment_content_admin_navigation_drawer).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return arrayListAnnouncement.size
    }
}