package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentSignInBinding
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentSignUpBinding

class SignUp : Fragment() {
    private lateinit var _binding : FragmentSignUpBinding
    private val binding get() = _binding.root
    private var userTitle : String? = null
    private lateinit var database : SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.signUp.setOnClickListener { signUp(view) }
        _binding.studentRadio.setOnClickListener {  onRadioButtonClicked(view)}
        _binding.lecturerRadio.setOnClickListener { onRadioButtonClicked(view) }

        _binding.titleRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.studentRadio -> userTitle = "Student"
                R.id.lecturerRadio -> userTitle = "Lecturer"
            }
        }

        activity?.let {
            database = it.openOrCreateDatabase("SCIS",Context.MODE_PRIVATE,null)

        }
    }
    fun onRadioButtonClicked(view: View) {}



    fun signUp(view:View){
        val userTRidNumber = _binding.tcKimlikNoEditText.text.toString()
        val userName = _binding.userNameEditText.text.toString()
        val userSurname = _binding.userSurnameEditText.text.toString()
        val userTelephoneNumber = _binding.telephoneNumberEditText.text.toString()
        val userMailAddress = _binding.mailAddressEditText.text.toString()

        println(userTitle)
        activity?.let{
            if(!userTRidNumber.equals("") && !userName.equals("") && !userSurname.equals("") && !userTelephoneNumber.equals("") && !userMailAddress.equals("") && userTitle != null){
                try {
                    val uniqId = uniqueAccount(userTRidNumber)
                    if(!uniqId){
                        val alertDialog = AlertDialog.Builder(it)
                        alertDialog.setTitle("Account available")
                        alertDialog.setMessage("There is an account opened with the ID number '${userTRidNumber}' you entered. Please login. If you have forgotten your password, please contact the system administrator.")
                        alertDialog.setNeutralButton("Okey",DialogInterface.OnClickListener { dialogInterface, i ->
                            val action = SignUpDirections.actionSignUpToSignIn()
                            Navigation.findNavController(view).navigate(action)
                        }).show()
                    }
                    else{
                        val loginInsertQuery = "INSERT INTO Tbl_Login (user_name, user_password,approval) VALUES(?, ?, ?)"
                        val statmentLogin = database.compileStatement(loginInsertQuery)
                        statmentLogin.bindString(1,userTRidNumber)
                        statmentLogin.bindString(2,userTRidNumber)
                        statmentLogin.bindString(3,"false")
                        statmentLogin.execute()

                        val cursorLogin = database.rawQuery("SELECT * FROM Tbl_Login ORDER BY ID DESC LIMIT 1",null)
                        val loginRawObjectIdIndex = cursorLogin.getColumnIndex("ID")
                        var loginObjectId : String? = null
                        while (cursorLogin.moveToNext()){
                            loginObjectId = cursorLogin.getString(loginRawObjectIdIndex)
                        }

                        val userInsertQuery = "INSERT INTO Tbl_User (loginId , user_TRid , user_name , user_surname , user_telephone ,user_mailAddress ,user_title) VALUES(?,?,?,?,?,?,?)"
                        val statmentUser = database.compileStatement(userInsertQuery)
                        statmentUser.bindString(1,loginObjectId)
                        statmentUser.bindString(2,userTRidNumber)
                        statmentUser.bindString(3,userName)
                        statmentUser.bindString(4,userSurname)
                        statmentUser.bindString(5,userTelephoneNumber)
                        statmentUser.bindString(6,userMailAddress)
                        statmentUser.bindString(7,userTitle)
                        statmentUser.execute()

                        cursorLogin.close()

                        val alertDialog = AlertDialog.Builder(it)
                        alertDialog.setTitle("User Registration Successful")
                        alertDialog.setMessage("User Registration Successful. Wait for the system administrator to approve your Account before you can Login to your Account. Thank you")
                        alertDialog.setNeutralButton("Ok!",DialogInterface.OnClickListener { dialogInterface, i ->
                            val action = SignUpDirections.actionSignUpToSignIn()
                            Navigation.findNavController(view).navigate(action)
                        }).show()
                        database.close()
                    }

                }
                catch (e : Exception){
                    println(e.localizedMessage)
                    e.printStackTrace()
                }
            }
            else{
                Toast.makeText(it, "Please Enter User Information",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uniqueAccount(accountId : String) : Boolean{
        try {
            val cursor = database.rawQuery("SELECT * FROM Tbl_Login WHERE user_name IN (?)", arrayOf(accountId))
            val accountIdIndx = cursor.getColumnIndex("user_name")
            while (cursor.moveToNext()){
                if(cursor.getString(accountIdIndx) == accountId){
                    return false
                }
            }
            cursor.close()
        }
        catch (e : Exception){
            e.printStackTrace()
        }
        return true
    }
}