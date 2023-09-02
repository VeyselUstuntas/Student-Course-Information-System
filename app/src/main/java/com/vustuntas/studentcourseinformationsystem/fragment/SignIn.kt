package com.vustuntas.studentcourseinformationsystem.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.activity.AdminNavigationDrawer
import com.vustuntas.studentcourseinformationsystem.activity.LecturerNavigationDrawer
import com.vustuntas.studentcourseinformationsystem.activity.StudentNavigationDrawer
import com.vustuntas.studentcourseinformationsystem.databinding.FragmentSignInBinding
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.lang.Exception


class SignIn : Fragment() {
    private lateinit var _binding : FragmentSignInBinding
    private val binding get() = _binding.root

    private lateinit var database : SQLiteDatabase

    private var userName : String? = null
    private var userPassword : String? = null

    private var singletonClass = PutUserInfo.userInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.signIn.setOnClickListener { signIn(view) }
        _binding.signUp.setOnClickListener { signUp(view) }

        _binding.showPasswordCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                _binding.userPasswordEditTextt.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else{
                _binding.userPasswordEditTextt.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        context?.let {
            database = it.openOrCreateDatabase("SCIS", Context.MODE_PRIVATE,null)
        }
    }

    fun signIn(view : View){
        userName = _binding.userNameEditText.text.toString()
        userPassword = _binding.userPasswordEditTextt.text.toString()

        if(!userPassword.equals("") && !userName.equals("")){
            try {
                println("deneme1")
                signInAccount()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        else{
            Toast.makeText(context,"Enter User Name and Password",Toast.LENGTH_SHORT).show()
        }
    }

    fun signUp(view: View){
        val action = SignInDirections.actionSignInToSignUp()
        Navigation.findNavController(view).navigate(action)
    }

    private fun signInAccount(){
        activity?.let {
            val cursor = database.rawQuery("SELECT * FROM Tbl_Login",null)

            val userNameIndx = cursor.getColumnIndex("user_name")
            val userPasswordIndx = cursor.getColumnIndex("user_password")

            var userApproval : String? = null       //userTitle
            var signInSucces = false                //girilen parola ve şifre kontrol
            var destination : String? = null        // uygulama yeniden açıldığında aynı kullanıcı hesabı ile başlatılması için

            while (cursor.moveToNext()){
                if(cursor.getString(userNameIndx) == userName && cursor.getString(userPasswordIndx) == userPassword){
                    val userApprovalIndx = cursor.getColumnIndex("approval")
                    userApproval = cursor.getString(userApprovalIndx)
                    signInSucces = true
                    destination = getDestination(userName!!)
                    if(userApproval.toBoolean())
                        putLastAccount(true,userName!!,userPassword!!)
                    break
                }
            }

            if(signInSucces){
                singletonClass.userId = userName
                if(userApproval.toBoolean() && destination != null){
                    if(destination.equals("admin")){
                        val intent = Intent(it,AdminNavigationDrawer::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        activity?.finish()
                    }
                    else if(destination.equals("Student")){
                        val intent = Intent(it,StudentNavigationDrawer::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        activity?.finish()
                    }
                    else{
                        val intent = Intent(it,LecturerNavigationDrawer::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        activity?.finish()
                    }
                }
                else{
                    val alertDialog = AlertDialog.Builder(it)
                    alertDialog.setTitle("Account Approval")
                    alertDialog.setMessage("You can login to your account after the System administrator approves it")
                    alertDialog.setNeutralButton("Ok!",DialogInterface.OnClickListener { dialogInterface, i ->
                        _binding.userNameEditText.text.clear()
                        _binding.userPasswordEditTextt.text.clear()
                    }).show()
                }
            }
            else{
                Toast.makeText(it,"username or password incorrect",Toast.LENGTH_LONG).show()
            }
            cursor.close()
        }
    }

    private fun getDestination(destinationUserAccount :String) : String{
        var destination : String = ""
        val destinationCursor = database.rawQuery("SELECT * FROM Tbl_User WHERE user_TRid = ?", arrayOf(destinationUserAccount))
        val destinationIndx = destinationCursor.getColumnIndex("user_title")
        while (destinationCursor.moveToNext()){
            destination = destinationCursor.getString(destinationIndx)
        }
        destinationCursor.close()
        return destination
    }

    private fun putLastAccount(accountAvailable : Boolean, accountName : String, accountPassword : String){
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS Tbl_LastAccount(ID INTEGER PRIMARY KEY , available VARCHAR, account_name VARCHAR, account_password VARCHAR)")
            val insertLastAccount = "INSERT INTO Tbl_LastAccount (available, account_name, account_password) VALUES (? ,? ,?)"
            val statment = database.compileStatement(insertLastAccount)
            statment.bindString(1,accountAvailable.toString())
            statment.bindString(2,accountName)
            statment.bindString(3,accountPassword)
            statment.execute()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}