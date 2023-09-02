package com.vustuntas.studentcourseinformationsystem.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.vustuntas.studentcourseinformationsystem.databinding.ActivityLecturerNavigationDrawerBinding
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.fragment.AnnouncementAndFoodListDirections
import com.vustuntas.studentcourseinformationsystem.helperClass.ChooseLectureDestination
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.io.ByteArrayOutputStream

class LecturerNavigationDrawer : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLecturerNavigationDrawerBinding

    private lateinit var database :SQLiteDatabase

    private var singletonClassUserInfo = PutUserInfo.userInfo

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private var selectedImageBitmap : Bitmap? = null

    private var lecturerImageView : ImageView? = null
    private var lecturerId : String? = null
    private var lecturerName : String? = null
    private var lecturerSurname : String? = null
    private var lecturerImageByteArray : ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = this.openOrCreateDatabase("SCIS", MODE_PRIVATE,null)
        lecturerId = singletonClassUserInfo.userId

        val userPassword =getUserPassword()
        if(userPassword.equals(lecturerId)){
            val alertDialog = AlertDialog.Builder(this@LecturerNavigationDrawer)
            alertDialog.setTitle("Password Change!")
            alertDialog.setMessage("Your default password is assigned as your username. You need to change your password. ")
            alertDialog.setPositiveButton("Okey!",DialogInterface.OnClickListener { dialogInterface, i ->

                val intent = Intent(this@LecturerNavigationDrawer,PasswordChange::class.java)
                startActivity(intent)
                ChooseLectureDestination.dest.destination = "Lecturer"
                finish()

            }).show()
        }
        else{

            binding = ActivityLecturerNavigationDrawerBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setSupportActionBar(binding.appBarLecturerNavigationDrawer.toolbar)

            val drawerLayout: DrawerLayout = binding.drawerLayout
            val navView: NavigationView = binding.navView
            val navController =
                findNavController(R.id.nav_host_fragment_content_lecturer_navigation_drawer)

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_announcementAndFoodList,R.id.nav_listMyCourses,R.id.nav_editStudentNotes,R.id.nav_accountLogOut
                ), drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            val headerView = navigationView.getHeaderView(0)
            val lecturerIdTextView = headerView.findViewById<TextView>(R.id.userIdTextView)
            val lecturerNameSurnameTextView = headerView.findViewById<TextView>(R.id.userNameSurnameTextView)
            lecturerImageView = headerView.findViewById(R.id.userImageImageView)

            registerLauncher()
            if(lecturerId != null){

                getLecturerInfo()
                lecturerIdTextView.setText(lecturerId)
                lecturerNameSurnameTextView.setText("$lecturerName $lecturerSurname")

                if(lecturerImageView != null && lecturerImageByteArray != null){
                    val imageBitmap = BitmapFactory.decodeByteArray(lecturerImageByteArray,0,lecturerImageByteArray!!.size)
                    lecturerImageView!!.setImageBitmap(imageBitmap)
                }
                else{
                    lecturerImageView!!.setImageResource(R.drawable.ic_launcher_foreground)
                }

            }
            if(lecturerImageView != null){
                lecturerImageView!!.setOnClickListener {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        if(ContextCompat.checkSelfPermission(this@LecturerNavigationDrawer,
                                Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                            //izin verilmedi izin iste
                            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){
                                Snackbar.make(it,"Should I be given access to the gallery?",Snackbar.LENGTH_INDEFINITE).setAction("Yes"){
                                    //izin iste
                                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                }.show()
                            }
                            else{
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        }
                        else{
                            //izin verildi Galeriye git
                            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            activityResultLauncher.launch(intentToGallery)
                        }
                    }
                    else{
                        if(ContextCompat.checkSelfPermission(this@LecturerNavigationDrawer,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            //izin verilmedi izin iste
                            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                                Snackbar.make(it,"Should I be given access to the gallery?",Snackbar.LENGTH_INDEFINITE).setAction("Yes"){
                                    //izin iste
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }.show()
                            }
                            else{
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                        else{
                            //izin verildi Galeriye git
                            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            activityResultLauncher.launch(intentToGallery)
                        }
                    }
                }
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.changePassword){
            val intent = Intent(this@LecturerNavigationDrawer,PasswordChange::class.java)
            startActivity(intent)
            ChooseLectureDestination.dest.destination = "Lecturer"
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_lecturer_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getLecturerInfo(){
        try {
            var cursor = database.rawQuery("SELECT * FROM Tbl_User WHERE user_TRid = ? ", arrayOf(lecturerId))
            val lecturerNameIndx = cursor.getColumnIndex("user_name")
            val lecturerSurnameIndx = cursor.getColumnIndex("user_surname")
            val lecturerImageIndx = cursor.getColumnIndex("user_image")
            while (cursor.moveToNext()){
                lecturerName = cursor.getString(lecturerNameIndx)
                lecturerSurname = cursor.getString(lecturerSurnameIndx)
                lecturerImageByteArray = cursor.getBlob(lecturerImageIndx)
            }
            cursor.close()
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val intentImage = it.data
                if(intentImage != null){
                    val imageUri = intentImage.data
                    if(imageUri != null){
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(this.contentResolver,imageUri)
                            selectedImageBitmap = ImageDecoder.decodeBitmap(source)
                            if(lecturerImageView != null)
                                lecturerImageView!!.setImageBitmap(selectedImageBitmap)
                            uploadStudentImage(selectedImageBitmap!!,lecturerId!!)

                        }
                        else{
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                            if(lecturerImageView != null)
                                lecturerImageView!!.setImageBitmap(selectedImageBitmap)
                            uploadStudentImage(selectedImageBitmap!!,lecturerId!!)
                        }
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@LecturerNavigationDrawer,"Not Allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reduceImageSize(selectedBitmap : Bitmap, maxSize : Int) : Bitmap {
        var width = selectedBitmap.width
        var height = selectedBitmap.height
        var bitmapRate = width.toDouble() / height.toDouble()

        if(bitmapRate > 1){
            var reducedHeight = (height * maxSize) / width.toDouble()
            height = reducedHeight.toInt()
            width = maxSize
        }
        else{
            var reducedWidth = (width * maxSize) / height.toDouble()
            width = reducedWidth.toInt()
            height = maxSize
        }

        return Bitmap.createScaledBitmap(selectedBitmap,width,height,false)
    }

    private fun uploadStudentImage(selectedImage : Bitmap, studentId : String){
        try {
            val reducedImageBitmap = reduceImageSize(selectedImage,300)
            val outPutStream = ByteArrayOutputStream()
            selectedImage.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteArrayImage = outPutStream.toByteArray()

            val updateStudentImageQuery = "UPDATE Tbl_User SET user_image = ? WHERE user_TRid = ?"
            val statament = database.compileStatement(updateStudentImageQuery)
            statament.bindBlob(1,byteArrayImage)
            statament.bindString(2,studentId)
            statament.execute()
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun getUserPassword():String{
        var userPassword : String = ""
        try {
            val cursor = database.rawQuery("select * from Tbl_Login WHERE user_name = ? ", arrayOf(lecturerId))
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