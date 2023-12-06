package com.example.mobliusstatus

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var filelist:ArrayList<DocumentFile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



//        <androidx.viewpager.widget.ViewPager
//        android:id="@+id/home_view_pager"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        app:layout_constraintEnd_toEndOf="parent"
//        app:layout_constraintStart_toStartOf="parent"
//        app:layout_constraintTop_toTopOf="parent">
//
//        <com.google.android.material.tabs.TabLayout
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"/>
//
//        </androidx.viewpager.widget.ViewPager>
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK){
            val treeUri = data?.data

            val sharedPreferences = requireActivity().getSharedPreferences("DATA_PATH",
                AppCompatActivity.MODE_PRIVATE
            )
            val myEdit = sharedPreferences.edit()
            myEdit.putString("PATH", treeUri.toString())
            myEdit.apply()
//        text = treeUri.toString()

            if (treeUri!=null){
                requireActivity().contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val files = DocumentFile.fromTreeUri(requireContext(), treeUri)
                for (file in files!!.listFiles()){
                    if (!file.name!!.endsWith(".nomedia")){
                        filelist.add(file)
                    }

                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFolderPermission(){
        val storageManager = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
        val targetDir = "Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
//        val targetDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/tree/")
        scheme += "%3A$targetDir"
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true)
        startActivityForResult(intent, 1233)
    }

    private fun saveStatus(statusFile: File) {
        // Copy the file to your app's directory
        val destFile = File(requireContext().getExternalFilesDir(null), statusFile.name)
        statusFile.copyTo(destFile)

        // Notify the media scanner to update the gallery
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(destFile.toString()),
            null,
            null
        )
    }
    private fun getStatusFiles(): List<File> {
        val statusDir = File(Environment.getExternalStorageDirectory().absolutePath + "/")
        Log.d("location", statusDir.toString())
        return if (statusDir.exists()) {
            statusDir.listFiles()?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

//        val statusDirectory = File(Environment.getExternalStorageDirectory().absolutePath + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses")

        filelist = ArrayList()

        val resultForPermission:Boolean = readDataFromPerfs()
        if(resultForPermission){
            val sh = requireActivity().getSharedPreferences("DATA_PATH", AppCompatActivity.MODE_PRIVATE)
            val uriPath = sh.getString("PATH","")
            requireActivity().contentResolver.takePersistableUriPermission(Uri.parse(uriPath),Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (uriPath!=null){
                val files = DocumentFile.fromTreeUri(requireContext(), Uri.parse(uriPath))
                for (file in files!!.listFiles()){
                    if (!file.name!!.endsWith(".nomedia")){
                        filelist.add(file)
                    }
                }
            }
        }else{
            getFolderPermission()
            readDataFromPerfs()
        }

        val adapter = StatusRVAdapter(view.context, filelist)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun readDataFromPerfs(): Boolean {
        val sh = requireActivity().getSharedPreferences("DATA_PATH", AppCompatActivity.MODE_PRIVATE)
        val uriPath = sh.getString("PATH","")
        if(uriPath!=null){
            if (uriPath.isEmpty()){
                return false
            }
        }
        return true
    }










    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}


