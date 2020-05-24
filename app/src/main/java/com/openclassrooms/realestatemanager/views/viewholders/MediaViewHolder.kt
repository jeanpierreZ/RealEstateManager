package com.openclassrooms.realestatemanager.views.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.databinding.MediaBinding
import com.openclassrooms.realestatemanager.models.Media
import java.lang.ref.WeakReference

class MediaViewHolder(val binding: MediaBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {
    // Represent a media of a real estate in the RecyclerView

    companion object {
        private val TAG = MediaViewHolder::class.java.simpleName
    }

    // For data
    private var callbackWeakRef: WeakReference<MediaAdapter.MediaListener>? = null
    private var callbackLongClickWeakRef: WeakReference<MediaAdapter.MediaLongClickListener>? = null

    fun updatePictures(media: Media?, glide: RequestManager,
                       callback: MediaAdapter.MediaListener,
                       callbackLongClick: MediaAdapter.MediaLongClickListener) {
        // Update widgets
        binding.mediaText.text = media?.mediaDescription

        if (media?.mediaPicture?.isAbsolute!!) {
            binding.mediaImage.let { glide.load(media.mediaPicture).into(it) }
        } else if (media.mediaVideo?.isAbsolute!!) {
            binding.mediaImage.let { glide.load(media.mediaVideo).into(it) }
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
