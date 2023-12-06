package com.example.mobliusstatus

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.io.File

public val storagePermissionCode = 1211
class MainActivity : AppCompatActivity() {
public lateinit var filelist:ArrayList<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findViewById<ViewPager>(R.id.navigation) as BottomNavigationView
        val fragmentManager = supportFragmentManager


//        start of bnv
        fragmentManager.beginTransaction().replace(R.id.nav_view, HomeFragment()).commit()
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
//        val fileArray = ArrayList<Any>()
//        val resultForPermission:Boolean = readDataFromPerfs()
//        if(resultForPermission){
//            val sh = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
//            val uriPath = sh.getString("PATH","")
//            contentResolver.takePersistableUriPermission(Uri.parse(uriPath),Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//            if (uriPath!=null){
//                val files = DocumentFile.fromTreeUri(applicationContext, Uri.parse(uriPath))
//                for (file in files!!.listFiles()){
//                    if (!file.name!!.endsWith(".nomedia")){
//                        fileArray.add(file)
//                    }
//                }
//            }
//        }else{
//            readDataFromPerfs()
//        }

    }


private fun readDataFromPerfs(): Boolean {
    val sh = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
    val uriPath = sh.getString("PATH","")
    if(uriPath!=null){
        if (uriPath.isEmpty()){
            return false
        }
    }
        return true
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

//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun getFolderPermission(){
//        val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
//        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
//        val targetDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
//        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
//        var scheme = uri.toString()
//        scheme = scheme.replace("/root/", ".tree/")
//        scheme += "%3A$targetDir"
//        uri = Uri.parse(scheme)
//        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
//        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true)
//        startActivityForResult(intent, 1233)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode== RESULT_OK){
//            val treeUri = data?.data
////            text = treeUri.toString()
//
//            if (treeUri!=null){
//                contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                val file = DocumentFile.fromTreeUri(applicationContext, treeUri)
//            }
//        }
//    }


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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode== RESULT_OK){
//            val treeUri = data?.data
//
//            val sharedPreferences = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
//            val myEdit = sharedPreferences.edit()
//            myEdit.putString("PATH", treeUri.toString())
//            myEdit.apply()
////        text = treeUri.toString()
//
//            if (treeUri!=null){
//                contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                val files = DocumentFile.fromTreeUri(applicationContext, treeUri)
//                for (file in files!!.listFiles()){
//                    if (!file.name!!.endsWith(".nomedia")){
//                        filelist.add(file)
//                    }
//
//                }
//            }
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun getFolderPermission(){
//        val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
//        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
//        val targetDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
//        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
//        var scheme = uri.toString()
//        scheme = scheme.replace("/root/", ".tree/")
//        scheme += "%3A$targetDir"
//        uri = Uri.parse(scheme)
//        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
//        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true)
//        startActivityForResult(intent, 1233)
//    }
}


