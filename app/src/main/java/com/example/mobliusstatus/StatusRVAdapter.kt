package com.example.mobliusstatus

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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
                    .thumbnail(0.1f)
                    .into(holder.statusImg)

//                    .transform(GlideRoundTransform(context, 16))
            }
        }
    }
    fun getFileExtension(file:DocumentFile): String? {
        val fileName = file.name ?: return null
        val dotIndex = fileName.lastIndexOf(".")
        return if (dotIndex > 0) fileName.substring(dotIndex + 1) else null
    }
}