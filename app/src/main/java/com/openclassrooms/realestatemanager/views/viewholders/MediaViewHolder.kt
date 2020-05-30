package com.openclassrooms.realestatemanager.views.viewholders

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.models.Media
import java.lang.ref.WeakReference

class MediaViewHolder(mediaView: View) : RecyclerView.ViewHolder(mediaView),
        View.OnClickListener, View.OnLongClickListener {
    // Represent a media of a real estate in the RecyclerView

    companion object {
        private val TAG = MediaViewHolder::class.java.simpleName
    }

    private var mediaText: TextView? = null
    private var mediaPicture: ImageView? = null
    private var mediaVideo: VideoView? = null

    // For data
    private var callbackWeakRef: WeakReference<MediaAdapter.MediaListener>? = null
    private var callbackLongClickWeakRef: WeakReference<MediaAdapter.MediaLongClickListener>? = null

    init {
        mediaText = mediaView.findViewById(R.id.media_text)
        mediaPicture = mediaView.findViewById(R.id.media_image)
        mediaVideo = mediaView.findViewById(R.id.media_video)
    }

    fun updateMedias(media: Media?, glide: RequestManager,
                     callback: MediaAdapter.MediaListener,
                     callbackLongClick: MediaAdapter.MediaLongClickListener,
                     context: Context) {
        // Description
        mediaText?.text = media?.mediaDescription

        // Video
        if (media?.mediaVideo != null && media.mediaVideo?.isAbsolute!!) {
            mediaVideo?.setVideoURI(media.mediaVideo)
            mediaVideo?.seekTo(1)
            val mediaController = MediaController(context)
            mediaVideo?.setMediaController(mediaController)
            mediaController.setAnchorView(mediaVideo)
            // Picture
        } else if (media?.mediaPicture != null && media.mediaPicture?.isAbsolute!!) {
            mediaPicture?.let { glide.load(media.mediaPicture).into(it) }
        }

        // Create news weak References to our Listeners and implement listeners

        this.callbackWeakRef = WeakReference(callback)
        itemView.setOnClickListener(this)

        this.callbackLongClickWeakRef = WeakReference(callbackLongClick)
        itemView.setOnLongClickListener(this)
    }

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the media position in the list
        val callback: MediaAdapter.MediaListener? = callbackWeakRef?.get()
        callback?.onClickMedia(adapterPosition)
        Log.d(TAG, "CLICK on media!")
    }

    override fun onLongClick(v: View?): Boolean {
        // When a long click happens, we fire our listener to get the media position in the list
        val callback: MediaAdapter.MediaLongClickListener? = callbackLongClickWeakRef?.get()
        callback?.onLongClickMedia(adapterPosition)
        Log.d(TAG, "LONG CLICK on media!")
        return true
    }
}
