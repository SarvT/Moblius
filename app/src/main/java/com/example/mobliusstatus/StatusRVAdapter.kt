package com.example.mobliusstatus

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class StatusRVAdapter(val context: Context, val fileArray:ArrayList<DocumentFile>): RecyclerView.Adapter<StatusRVAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val statusText:TextView = view.findViewById(R.id.statusText)
        val statusImg:ImageView = view.findViewById(R.id.statusImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.status_recycler_view_item, parent,false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = fileArray[position]
        if (currentItem!=null){
            holder.statusText?.text = currentItem.name

            val imgFileExtensions = arrayOf("jpeg", "jpg", "png")
            val vidFileExtensions = arrayOf("mp4", "webm")
            val nameExt = getFileExtension(currentItem)
            if (imgFileExtensions.contains(nameExt)){
                Glide.with(context).load(Uri.parse(currentItem.uri.toString())).into(holder.statusImg)
            } else if(vidFileExtensions.contains(nameExt)){
//                val bmThumbnail = ThumbnailUtils.createVideoThumbnail(currentItem.uri.toString(), MediaStore.Images.Thumbnails.MINI_KIND);
//                holder.statusImg.setImageBitmap(bmThumbnail);
                Glide.with(context)
                    .load(Uri.parse(currentItem.uri.toString()))
                    .thumbnail(0.4f)
                    .into(holder.statusImg)

//                    .transform(GlideRoundTransform(context, 16))
            }
        }
        holder.statusImg.setOnClickListener {
            Log.d("click", "${currentItem.name}")
//            saveStatus(currentItem)
            askAgain(context, currentItem)
        }
    }
    fun getFileExtension(file:DocumentFile): String? {
        val fileName = file.name ?: return null
        val dotIndex = fileName.lastIndexOf(".")
        return if (dotIndex > 0) fileName.substring(dotIndex + 1) else null
    }

    fun askAgain(context: Context, file: DocumentFile){
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        saveStatus(file)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {

                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to save the status?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()

    }
    private fun saveStatus(statusFile: DocumentFile) {
        // Copy the file to your app's directory
//        val destFile = File(context.getExternalFilesDir(null), statusFile.name)
//        statusFile.copyTo(destFile)
        val inputStream = context.contentResolver.openInputStream(Uri.parse(statusFile.uri.toString()))
        val vidFileExtensions = arrayOf("mp4", "webm")
        val nameExt = getFileExtension(statusFile)
        if(vidFileExtensions.contains(nameExt)){
            val fileName = "${System.currentTimeMillis()}.mp4"
            try {
                val vals = ContentValues()
                vals.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                vals.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                vals.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS+"/Moblius/")
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"),vals)
                val outputStream = uri?.let {
                    context.contentResolver.openOutputStream(it)
                }!!
                if (inputStream!=null){
                    outputStream.write(inputStream.readBytes())
                }
                outputStream.close()
                Toast.makeText(context, "VIDEO HAS BEEN SAVED TO YOUR GALLERY!", Toast.LENGTH_SHORT).show()


            }catch (e:IOException){
                Toast.makeText(context, "SOME ERROR OCCURRED!", Toast.LENGTH_SHORT).show()

            }
        }else{
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(statusFile.uri.toString()))
            val fileName = "${System.currentTimeMillis()}.jpg"
            var outputStream:OutputStream?=null
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                context.contentResolver.also { contentResolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    outputStream = imgUri?.let { contentResolver.openOutputStream(it) }
                }
            } else {
                val imgDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val img = File(imgDir, fileName)
                outputStream = FileOutputStream(img)
            }
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(context, "IMAGE HAS BEEN SAVED TO YOUR GALLERY!", Toast.LENGTH_SHORT).show()


            }
        }

        // Notify the media scanner to update the gallery
//        MediaScannerConnection.scanFile(
//            context,
//            arrayOf(destFile.toString()),
//            null,
//            null
//        )
    }
}