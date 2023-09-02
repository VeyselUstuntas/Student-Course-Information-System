package com.vustuntas.studentcourseinformationsystem.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentUpdateAndDeleteFoodListBinding
import java.util.Calendar

class UpdateAndDeleteFoodList : Fragment() {
    private lateinit var _binding : FragmentUpdateAndDeleteFoodListBinding
    private val binding get() = _binding.root
    private lateinit var calendar : Calendar
    private var foodId : Int? = null
    private lateinit var database: SQLiteDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateAndDeleteFoodListBinding.inflate(inflater,container,false)
        _binding.menuDateEditText.setOnClickListener { selectDate(it) }
        _binding.deleteFood.setOnClickListener { deleteFood(it) }
        _binding.updateFood.setOnClickListener { updateFood(it) }
        calendar = Calendar.getInstance()
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        arguments?.let {
            foodId = UpdateAndDeleteFoodListArgs.fromBundle(it).foodId
        }

        getFoodInformation()
    }

    private fun selectDate(view:View){
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        activity?.let {
            val datePickerDialog = DatePickerDialog(it, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                var month = i2
                month++
                val formatMonth = if(month < 10) "0$month" else "$month"
                val formatDay = if(i3 < 10) "0$i3" else "$i3"
                val selectedDate = "$formatDay/${formatMonth}/$i"
                _binding.menuDateEditText.setText(selectedDate)
            },year,month,day).show()
        }
    }

    private fun getFoodInformation(){
        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_Foods WHERE ID = ? ", arrayOf(foodId.toString()))
            val menuDateIndx = cursor.getColumnIndex("food_menuDate")
            val soupNameIndx = cursor.getColumnIndex("food_soupName")
            val mainFoodIndx = cursor.getColumnIndex("food_mainFood")
            while (cursor.moveToNext()){
                _binding.menuDateEditText.setText(cursor.getString(menuDateIndx))
                _binding.soupNameEditText.setText(cursor.getString(soupNameIndx))
                _binding.mainFoodEditText.setText(cursor.getString(mainFoodIndx))
            }
            cursor.close()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun deleteFood(view:View){
        try {
            val deleteFoodQuery = "DELETE FROM Tbl_Foods WHERE ID = ?"
            val statment = database.compileStatement(deleteFoodQuery)
            statment.bindString(1,foodId.toString())
            statment.execute()
            Toast.makeText(context,"Selected Food Information Deleted",Toast.LENGTH_SHORT).show()
            val action = UpdateAndDeleteFoodListDirections.actionUpdateAndDeleteFoodListToNavAddFoodList()
            Navigation.findNavController(view).navigate(action)
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun updateFood(view:View){
        try {
            val updateFoodQuary = "UPDATE Tbl_Foods SET food_menuDate = ?, food_soupName = ?, food_mainFood = ? WHERE ID = ?"
            val statament = database.compileStatement(updateFoodQuary)
            statament.bindString(1,_binding.menuDateEditText.text.toString())
            statament.bindString(2,_binding.soupNameEditText.text.toString())
            statament.bindString(3,_binding.mainFoodEditText.text.toString())
            statament.bindString(4,foodId.toString())
            statament.execute()
            Toast.makeText(context,"Selected Food Information Updated",Toast.LENGTH_SHORT).show()
            val action = UpdateAndDeleteFoodListDirections.actionUpdateAndDeleteFoodListToNavAddFoodList()
            Navigation.findNavController(view).navigate(action)
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }
}