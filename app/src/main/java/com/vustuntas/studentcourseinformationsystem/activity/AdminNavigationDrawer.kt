package com.vustuntas.studentcourseinformationsystem.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.vustuntas.studentcourseinformationsystem.databinding.ActivityAdminNavigationDrawerBinding
import com.vustuntas.studentcourseinformationsystem.R
import com.vustuntas.studentcourseinformationsystem.helperClass.ChooseLectureDestination
import com.vustuntas.studentcourseinformationsystem.helperClass.PutUserInfo
import java.io.ByteArrayOutputStream
import java.lang.Exception

class AdminNavigationDrawer : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAdminNavigationDrawerBinding
    private lateinit var database : SQLiteDatabase

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    private var adminId : String? = null
    private var adminName : String? = null
    private var adminSurname : String? = null
    private var adminImageByteArray : ByteArray? = null

    private val singletonClass = PutUserInfo.userInfo

    private var selectedAdminImageBitmap :Bitmap? = null
    private var adminImageView : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarAdminNavigationDrawer.toolbarr)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_admin_navigation_drawer)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_addCourse,R.id.nav_userConfirm,R.id.nav_addAnnouncement,R.id.nav_addFoodList,R.id.nav_accountLogOut
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        database = this.openOrCreateDatabase("SCIS", MODE_PRIVATE,null)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)

        var adminIdTextView = headerView.findViewById<TextView>(R.id.userId)
        var adminNameSurnameTextView = headerView.findViewById<TextView>(R.id.userNameSurname)
        adminImageView = headerView.findViewById<ImageView>(R.id.userImage)

        resultLauncher()

        adminId = singletonClass.userId
        if(adminId != null){
            getAdminInfo(adminId!!)
            adminIdTextView.text = adminId
            adminNameSurnameTextView.text = "${adminName} ${adminSurname}"

            if(adminImageView!= null && adminImageByteArray != null){
                val adminImageBitmap = BitmapFactory.decodeByteArray(adminImageByteArray,0,adminImageByteArray!!.size)
                adminImageView!!.setImageBitmap(adminImageBitmap)
            }
            else{
                adminImageView!!.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
        if(adminImageView!= null){
            adminImageView!!.setOnClickListener{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if(ContextCompat.checkSelfPermission(this@AdminNavigationDrawer,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                        //izin verilmedi izin al
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                            Snackbar.make(it,"Should I be given access to the gallery?",Snackbar.LENGTH_INDEFINITE).setAction("Yes!"){
                                //izin iste
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }.show()
                        }
                        else{
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                        }
                    }
                    else{
                        //izin verildi Galeriye Git
                        val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
                    }
                }
                else{
                    if(ContextCompat.checkSelfPermission(this@AdminNavigationDrawer,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //izin verilmedi izin al
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                            Snackbar.make(it,"Should I be given access to the gallery?",Snackbar.LENGTH_INDEFINITE).setAction("Yes!"){
                                //izin iste
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                            }.show()
                        }
                        else{
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                    else{
                        //izin verildi Galeriye Git
                        val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
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
            val intent = Intent(this@AdminNavigationDrawer,PasswordChange::class.java)
            startActivity(intent)
            ChooseLectureDestination.dest.destination = "Admin"
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getAdminInfo(adminID :String){
        try {
            val adminCursor = database.rawQuery("SELECT * FROM Tbl_User WHERE user_TRid = ?", arrayOf(adminID))
            val adminNameIndx = adminCursor.getColumnIndex("user_name")
            val adminSurnameIndx = adminCursor.getColumnIndex("user_surname")
            val adminImageIndx = adminCursor.getColumnIndex("user_image")
            while (adminCursor.moveToNext()){
                adminName = adminCursor.getString(adminNameIndx)
                adminSurname = adminCursor.getString(adminSurnameIndx)
                adminImageByteArray = adminCursor.getBlob(adminImageIndx)
            }
            adminCursor.close()
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun resultLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == RESULT_OK){
                val intent = result.data
                if(intent!=null){
                    val imageUri = intent.data
                    if(imageUri != null){
                        if(adminImageView!= null){
                            if(Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(this.contentResolver,imageUri)
                                selectedAdminImageBitmap = ImageDecoder.decodeBitmap(source)
                                uploadUserImage(selectedAdminImageBitmap!!,adminId!!)
                                adminImageView!!.setImageBitmap(selectedAdminImageBitmap)
                            }
                            else{
                                selectedAdminImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                                uploadUserImage(selectedAdminImageBitmap!!,adminId!!)
                                adminImageView!!.setImageBitmap(selectedAdminImageBitmap)
                            }
                        }
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else
                Toast.makeText(this@AdminNavigationDrawer,"Not Allowed!",Toast.LENGTH_SHORT).show()

        }
    }

    private fun reduceImageSize(selectedBitmap : Bitmap, maxSize : Int) : Bitmap{
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

    private fun uploadUserImage(userImage : Bitmap,userId : String){
        try {
            if(selectedAdminImageBitmap != null){
                val reducedBitmapImage = reduceImageSize(selectedAdminImageBitmap!!,300)

                val outStreamByteArray = ByteArrayOutputStream()
                reducedBitmapImage.compress(Bitmap.CompressFormat.PNG,50,outStreamByteArray)
                val byteArrayImage = outStreamByteArray.toByteArray()

                val updateImageQuery = "UPDATE Tbl_User SET user_image = ? WHERE user_TRid = ? "
                val statment = database.compileStatement(updateImageQuery)
                statment.bindBlob(1,byteArrayImage)
                statment.bindString(2,userId)
                statment.execute()
            }
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }
}