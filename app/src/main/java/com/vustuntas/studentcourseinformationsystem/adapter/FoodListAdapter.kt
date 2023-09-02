package com.vustuntas.studentcourseinformationsystem.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.studentcourseinformationsystem.helperClass.FoodListInfo
import java.util.ArrayList
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AddFoodListDirections

class FoodListAdapter (val foodArrayList: ArrayList<FoodListInfo>, val activity : Activity): RecyclerView.Adapter<FoodListAdapter.FoodListVH>() {
    class FoodListVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_add_food_list,parent,false)
        return FoodListVH(itemView)
    }

    override fun onBindViewHolder(holder: FoodListVH, position: Int) {
        val foodInfoObject = foodArrayList.get(position) as FoodListInfo
        holder.itemView.findViewById<TextView>(R.id.recycler_row_add_food_list_foodDateTextView).text = foodInfoObject.menuDate
        holder.itemView.setOnClickListener {
            val action = AddFoodListDirections.actionNavAddFoodListToUpdateAndDeleteFoodList(foodInfoObject.foodId)
            Navigation.findNavController(activity, R.id.nav_host_fragment_content_admin_navigation_drawer).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return foodArrayList.size
    }
}