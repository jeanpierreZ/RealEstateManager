package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.views.viewholders.MediaViewHolder


class MediaAdapter(private var list: ArrayList<Media?>,
                   private val glide: RequestManager,
                   private val callback: MediaListener,
                   private val callbackLongClick: MediaLongClickListener,
                   private var context: Context)
    : RecyclerView.Adapter<MediaViewHolder>() {

    //----------------------------------------------------------------------------------
    // Callbacks

    interface MediaListener {
        fun onClickMedia(position: Int)
    }

    interface MediaLongClickListener {
        fun onLongClickMedia(position: Int)
    }

    //----------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.media, parent, false)
        return MediaViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) =
            holder.updateMedias(list[position], glide, callback, callbackLongClick, context)

    fun setMedias(mediaList: ArrayList<Media?>) {
        list = mediaList
        notifyDataSetChanged()
    }

    // Return the position of a media in the list
    fun getPosition(position: Int): Media? = list[position]
}
