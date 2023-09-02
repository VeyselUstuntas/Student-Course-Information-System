package com.vustuntas.studentcourseinformationsystem.activity

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.ActivityPasswordChangeBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.ChooseLectureDestination
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo

class PasswordChange : AppCompatActivity() {
    private lateinit var binding:ActivityPasswordChangeBinding
    private var singletonUserId = PutUserInfo.userInfo
    private lateinit var database: SQLiteDatabase
    private var destination :String? = null
    private var userId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = this.openOrCreateDatabase("SCIS", MODE_PRIVATE,null)
    }

    fun changePassword(view: View){
        val oldPassword = binding.oldPasswordEditText.text.toString()
        val newPasword = binding.newPasswordEditText.text.toString()
        val newPasswordRepeat = binding.newPasswordRepeatEditText.text.toString()
        userId = singletonUserId.userId

        if(oldPassword.equals(getUserPassword())){
            if(newPasword.equals(newPasswordRepeat)){
                try {
                    destination = ChooseLectureDestination.destination
                    println(destination)
                    val updatePassword = "UPDATE Tbl_Login SET user_password = ? WHERE user_name = ? "
                    val statment = database.compileStatement(updatePassword)
                    statment.bindString(1,newPasword)
                    statment.bindString(2,userId)
                    statment.execute()
                    Toast.makeText(this@PasswordChange,"Password Changed", Toast.LENGTH_SHORT).show()
                    if(destination.equals("Lecturer")){
                        val intent = Intent(this@PasswordChange,LecturerNavigationDrawer::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(destination.equals("Student")){
                        val intent = Intent(this@PasswordChange,StudentNavigationDrawer::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this@PasswordChange,AdminNavigationDrawer::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                catch (E:Exception){
                    println(E.localizedMessage)
                }
            }
            else
                Toast.makeText(this@PasswordChange,"Both passwords must match each other", Toast.LENGTH_SHORT).show()

        }
        else
            Toast.makeText(this@PasswordChange,"Enter Your Old Password Correctly", Toast.LENGTH_SHORT).show()

    }

    private fun getUserPassword():String{
        var userPassword : String = ""
        try {
            val cursor = database.rawQuery("select * from Tbl_Login WHERE user_name = ? ", arrayOf(userId))
            val userPasseordIndx = cursor.getColumnIndex("user_password")
            while (cursor.moveToNext()){
                userPassword = cursor.getString(userPasseordIndx)
            }
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }
        return userPassword
    }
}