package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Media
import com.openclassrooms.realestatemanager.views.viewholders.MediaFullScreenViewHolder
import com.openclassrooms.realestatemanager.views.viewholders.MediaViewHolder


class MediaAdapter(private var list: ArrayList<Media?>,
                   private val glide: RequestManager,
                   private val callbackMedia: MediaListener,
                   private val callbackMediaLongClick: MediaLongClickListener,
                   private val callbackFullScreen: MediaFullScreenListener,
                   private val isFullScreen: Boolean,
                   private val isRealEstateActivity: Boolean,
                   private var context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_MEDIA = 1
        const val TYPE_MEDIA_FULL_SCREEN = 2
    }

    //----------------------------------------------------------------------------------
    // Callbacks

    interface MediaListener {
        fun onClickMedia(position: Int)
    }

    interface MediaLongClickListener {
        fun onLongClickMedia(position: Int)
    }

    interface MediaFullScreenListener {
        fun onClickMediaFullScreen(position: Int)
    }

    //----------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        return if (viewType == TYPE_MEDIA) {
            val view = inflater.inflate(R.layout.media, parent, false)
            MediaViewHolder(view)
        } else {
            // For full screen layout
            val view = inflater.inflate(R.layout.media_full_screen, parent, false)
            MediaFullScreenViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_MEDIA) {
            (holder as MediaViewHolder).updateMedias(list[position], position, list.size, glide, callbackMedia, callbackMediaLongClick, isRealEstateActivity, context)
        } else {
            // For full screen layout
            (holder as MediaFullScreenViewHolder).updateMediasFullScreen(list[position], glide, callbackFullScreen, context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isFullScreen) {
            TYPE_MEDIA_FULL_SCREEN
        } else {
            TYPE_MEDIA
        }
    }

    override fun getItemCount(): Int = list.size

    fun setMedias(mediaList: ArrayList<Media?>) {
        list = mediaList
        notifyDataSetChanged()
    }

    // Return the position of a media in the list
    fun getPosition(position: Int): Media? = list[position]

}
