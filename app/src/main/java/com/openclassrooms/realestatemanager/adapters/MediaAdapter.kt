package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.databinding.MediaBinding
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.views.viewholders.MediaViewHolder


class MediaAdapter(private var list: ArrayList<Media?>,
                   private val glide: RequestManager,
                   private val callback: MediaListener,
                   private val callbackLongClick: MediaLongClickListener)
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
        val inflater = LayoutInflater.from(parent.context)
        val binding = MediaBinding.inflate(inflater, parent, false)
        return MediaViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) =
            holder.updateMedias(list[position], glide, callback, callbackLongClick)

    fun setMedias(mediaList: ArrayList<Media?>) {
        list = mediaList
        notifyDataSetChanged()
    }

    // Return the position of a media in the list
    fun getPosition(position: Int): Media? = list[position]
}
