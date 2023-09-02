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
import androidx.recyclerview.widget.LinearLayoutManager
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.adapter.FoodListAdapter
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentAddFoodListBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.FoodListInfo
import java.lang.Exception
import java.util.ArrayList
import java.util.Calendar

class AddFoodList : Fragment() {
    private lateinit var _binding : FragmentAddFoodListBinding
    private val binding get() = _binding.root
    private lateinit var database : SQLiteDatabase
    private var menuDate : String = ""
    private var soupName : String = ""
    private var mainFood : String = ""
    private lateinit var foodArrayList : ArrayList<FoodListInfo>
    private lateinit var adapter : FoodListAdapter
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFoodListBinding.inflate(inflater,container,false)
        _binding.saveFoodButton.setOnClickListener { saveFood(it) }
        _binding.menuDate.setOnClickListener { selectDate(it) }
        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)
        }
        foodArrayList = ArrayList<FoodListInfo>()
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            adapter = FoodListAdapter(foodArrayList,it)
        }
        val layoutManager = LinearLayoutManager(context)
        _binding.recyclerViewAddFoodList.layoutManager = layoutManager
        _binding.recyclerViewAddFoodList.adapter = adapter
        getFoodList()
    }

    private fun selectDate(view: View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        activity?.let {
            val datePickerDialog = DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    val formattedDay = if (i3 < 10) "0$i3" else "$i3"
                    val formattedMonth = if (i2 < 9) "0${i2 + 1}" else "${i2 + 1}"
                    val selectedDate = "$formattedDay/$formattedMonth/$i"
                    _binding.menuDate.setText(selectedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }


    private fun saveFood(view : View){
        menuDate = _binding.menuDate.text.toString()
        soupName = _binding.soupNameEditText.text.toString()
        mainFood = _binding.mealNameEditText.text.toString()

        if(!menuDate.equals("") && !soupName.equals("") && !mainFood.equals("")){
            try {
                val insertFoodQuery = "INSERT INTO Tbl_Foods (food_menuDate,food_soupName, food_mainFood) VALUES(?,?,?)"
                val statment = database.compileStatement(insertFoodQuery)
                statment.bindString(1,menuDate)
                statment.bindString(2,soupName)
                statment.bindString(3,mainFood)
                statment.execute()
                Toast.makeText(context,"Food successfully added",Toast.LENGTH_SHORT).show()
                _binding.menuDate.text.clear()
                _binding.mealNameEditText.text.clear()
                _binding.soupNameEditText.text.clear()
                getFoodList()
            }
            catch (e :Exception){
                e.printStackTrace()
            }
        }
        else
            Toast.makeText(context,"Please Enter Food Details",Toast.LENGTH_SHORT).show()

    }

    private fun getFoodList(){
        try {
            foodArrayList.clear()
            val cursor = database.rawQuery("SELECT * FROM Tbl_Foods",null)
            val foodIdIndx = cursor.getColumnIndex("ID")
            val menuDateIndx = cursor.getColumnIndex("food_menuDate")
            val soupNameIndx = cursor.getColumnIndex("food_soupName")
            val mainFoodIndx = cursor.getColumnIndex("food_mainFood")
            while (cursor.moveToNext()){
                val foodInfoObject = FoodListInfo(
                    cursor.getInt(foodIdIndx),
                    cursor.getString(menuDateIndx),
                    cursor.getString(soupNameIndx),
                    cursor.getString(mainFoodIndx))
                foodArrayList.add(foodInfoObject)
            }
            adapter.notifyDataSetChanged()
            cursor.close()

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}