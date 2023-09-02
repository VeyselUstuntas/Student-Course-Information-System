package com.vustuntas.studentcourseinformationsystem.activity

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vustuntas.studentcourseinformationsystem.databinding.ActivityMainBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPreferences : SharedPreferences
    private var regis : Boolean = false
    private var accountAvailable : String? = null
    private var destinationUserAccount : String? = null
    private lateinit var database : SQLiteDatabase
    private val singletonClass = PutUserInfo.userInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("com.vustuntas.studentcourseinformationsystem", MODE_PRIVATE)
        database = this.openOrCreateDatabase("SCIS", MODE_PRIVATE,null)

        createAdminAccount()

        val lastUser = getLastAccount()
        accountAvailable = lastUser.get("available")
        destinationUserAccount = lastUser.get("accountName")

        if(destinationUserAccount != null)
            currentUserSignIn(destinationUserAccount!!)

    }

    private fun createAdminAccount(){
        try {
            regis = sharedPreferences.getBoolean("status",false)
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_Login(ID INTEGER PRIMARY KEY, user_name VARCHAR, user_password VARCHAR, approval VARCHAR)")
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_User(ID INTEGER PRIMARY KEY, loginId VARCHAR, user_TRid VARCHAR, user_name VARCHAR, user_surname VARCHAR, user_telephone VARCHAR, " +
                    "user_mailAddress VARCHAR, user_title VARCHAR, user_image BLOB)")
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_Foods(ID INTEGER PRIMARY KEY, food_menuDate VARCHAR, food_soupName VARCHAR, food_mainFood VARCHAR)")
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_Announcement(ID INTEGER PRIMARY KEY, announcement_title VARCHAR, announcement_content VARCHAR)")
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_SelectedCourses(ID INTEGER PRIMARY KEY, student_id VARCHAR, course_id VARCHAR, course_code VARCHAR)")
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_StudentsNote(ID INTEGER PRIMARY KEY, studentId VARCHAR, lecturerId VARCHAR, selectedCourseId VARCHAR, midterm_grade VARCHAR, final_grade VARCHAR, letter_grade VARCHAR)")

            if(!regis){
                database.execSQL("INSERT INTO Tbl_Login (user_name, user_password, approval) VALUES('25759516954','veysel','true')")
                val cursor = database.rawQuery("SELECT  * FROM Tbl_Login ORDER BY ID DESC LIMIT 1",null)
                val loginIdIndx = cursor.getColumnIndex("ID")
                var loginId : Int? = null
                while (cursor.moveToNext()){
                    loginId = cursor.getInt(loginIdIndx)
                    println(loginId)
                }
                if(loginId != null){
                    val queryInsertUser = "INSERT INTO Tbl_User (loginId, user_TRid, user_name, user_surname, user_telephone, user_mailAddress, user_title,user_image) VALUES(?, ?, ?, ?, ?, ?, ?,?)"
                    val statment = database.compileStatement(queryInsertUser)
                    statment.bindString(1,loginId.toString())
                    statment.bindString(2,"25759516954")
                    statment.bindString(3,"veysel")
                    statment.bindString(4,"ustuntas")
                    statment.bindString(5,"05464263990")
                    statment.bindString(6,"veyselustuntas@gmail.com")
                    statment.bindString(7,"admin")
                    val byteArray = ByteArray(1)
                    statment.bindBlob(8,byteArray)
                    statment.execute()
                }

                //admin hesabı sürekli oluşturmaması için
                sharedPreferences.edit().putBoolean("status",true).apply()

                cursor.close()
                database.close()
            }
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun getDestination() : String{
        var destination : String = ""
        val database = this.openOrCreateDatabase("SCIS", MODE_PRIVATE,null)
        val destinationCursor = database.rawQuery("SELECT * FROM Tbl_User WHERE user_TRid = ?", arrayOf(destinationUserAccount))
        val destinationIndx = destinationCursor.getColumnIndex("user_title")
        while (destinationCursor.moveToNext()){
            destination = destinationCursor.getString(destinationIndx)
        }
        return destination
    }

    private fun getLastAccount() : HashMap<String,String>{
        var lastUserInfo = HashMap<String,String>()
        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_LastAccount",null)
            val availableIndx = cursor.getColumnIndex("available")
            val userNameIndx = cursor.getColumnIndex("account_name")
            val userPasswordIndx = cursor.getColumnIndex("account_password")
            while (cursor.moveToNext())
            {
                lastUserInfo.put("available",cursor.getString(availableIndx))
                lastUserInfo.put("accountName",cursor.getString(userNameIndx))
                lastUserInfo.put("accountPassword",cursor.getString(userPasswordIndx))
            }
            cursor.close()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
        return lastUserInfo
    }


    private fun currentUserSignIn(userId : String){

        if(accountAvailable.toBoolean()){
            singletonClass.userId = userId
            val destination = getDestination()
            if(destination.equals("admin")){
                val intent = Intent(this@MainActivity, AdminNavigationDrawer::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }
            else if(destination.equals("Student")){
                val intent = Intent(this@MainActivity, StudentNavigationDrawer::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }
            else{
                val intent = Intent(this@MainActivity, LecturerNavigationDrawer::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }
        }

    }
}