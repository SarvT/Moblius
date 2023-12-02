package com.example.mobliusstatus

import android.content.ClipData.Item
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.io.File

public val storagePermissionCode = 1211
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findViewById<ViewPager>(R.id.navigation) as BottomNavigationView
        val fragmentManager = supportFragmentManager


//        start of bnv
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    fragmentManager.beginTransaction().replace(R.id.nav_view, HomeFragment())
                        .commit()
                    true
                }
                R.id.action_img -> {
                    fragmentManager.beginTransaction().replace(R.id.nav_view, ImgFragment())
                        .commit()
                    true
                }
                R.id.action_vid -> {
                    fragmentManager.beginTransaction().replace(R.id.nav_view, VidFragment())
                        .commit()
                    true
                }
                else -> {
                    fragmentManager.beginTransaction().replace(R.id.nav_view, HomeFragment())
                        .commit()
                    true
                }
            }
        }
//        end of bnv
        checkForPermisiion()


    }



    private fun checkForPermisiion(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1307)
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1307){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permitted!!", Toast.LENGTH_SHORT).show()
                Log.d("Permission", "Granted")

            }
            else{
                Log.d("Permission", "Not Granted")
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1307)
            }
        }
    }


    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }



//    statuses

    // Example function to save a status file
    private fun saveStatus(statusFile: File) {
        // Copy the file to your app's directory
        val destFile = File(getExternalFilesDir(null), statusFile.name)
        statusFile.copyTo(destFile)

        // Notify the media scanner to update the gallery
        MediaScannerConnection.scanFile(
            applicationContext,
            arrayOf(destFile.toString()),
            null,
            null
        )
    }
    private fun getStatusFiles(): List<File> {
        val statusDir = File(Environment.getExternalStorageDirectory().toString() + "Android/media/com.whatsapp//WhatsApp/Media/.Statuses")

        return if (statusDir.exists()) {
            statusDir.listFiles()?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}

