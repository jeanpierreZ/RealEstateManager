package com.openclassrooms.realestatemanager.viewholders

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.models.Media
import java.lang.ref.WeakReference


class MediaViewHolder(mediaView: View) : RecyclerView.ViewHolder(mediaView),
        View.OnClickListener, View.OnLongClickListener {
    //  Represent a real estate media in the RecyclerView

    companion object {
        private val TAG = MediaViewHolder::class.java.simpleName
    }

    private var currentPageView: TextView? = null
    private var textView: TextView? = null
    private var imageView: ImageView? = null
    private var playerView: PlayerView? = null
    private var fullScreenIconView: ImageView? = null

    // For data
    private var callbackWeakRef: WeakReference<MediaAdapter.MediaListener>? = null
    private var callbackLongClickWeakRef: WeakReference<MediaAdapter.MediaLongClickListener>? = null

    init {
        currentPageView = mediaView.findViewById(R.id.media_current_page)
        textView = mediaView.findViewById(R.id.media_text)
        imageView = mediaView.findViewById(R.id.media_image)
        playerView = mediaView.findViewById(R.id.media_player_video)
        fullScreenIconView = mediaView.findViewById(R.id.exo_fullscreen)
    }

    fun updateMedias(media: Media?, position: Int, listSize: Int, glide: RequestManager,
                     callback: MediaAdapter.MediaListener,
                     callbackLongClick: MediaAdapter.MediaLongClickListener,
                     isRealEstateActivity: Boolean,
                     context: Context) {
        // Current page
        val page = position + 1
        currentPageView?.text = context.getString(R.string.page, page, listSize)

        // Description
        textView?.text = media?.mediaDescription

        // Video
        if (media?.mediaVideo != null && media.mediaVideo?.isAbsolute!!) {
            imageView?.visibility = View.GONE

            if (isRealEstateActivity) {
                fullScreenIconView?.visibility = View.GONE
            }

            // Build SimpleExoPlayer
            val player = SimpleExoPlayer.Builder(context).build()
            // Bind the player to the view.
            playerView?.player = player

            // Add a listener to receive events from the player.
            player.addListener(object : Player.EventListener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        playerView?.hideController()
                    } else {
                        playerView?.showController()
                    }
                }
            })

            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, BuildConfig.APPLICATION_ID))
            // This is the MediaSource representing the media to be played.
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(media.mediaVideo)
            // Prepare the player with the source.
            player.prepare(videoSource)

            // Picture
        } else if (media?.mediaPicture != null && media.mediaPicture?.isAbsolute!!) {
            playerView?.visibility = View.GONE
            imageView?.let { glide.load(media.mediaPicture).into(it) }
        }

        // Create news weak References to our Listeners and implement listeners

        callbackWeakRef = WeakReference(callback)
        fullScreenIconView?.setOnClickListener(this)
        imageView?.setOnClickListener(this)

        callbackLongClickWeakRef = WeakReference(callbackLongClick)
        playerView?.videoSurfaceView?.setOnClickListener { view ->
            view.setOnLongClickListener(this)
            view.setOnClickListener { playerView?.showController() }
        }
        imageView?.setOnLongClickListener(this)
    }

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the media position in the list
        val callback: MediaAdapter.MediaListener? = callbackWeakRef?.get()
        callback?.onClickMedia(adapterPosition)
        // Pause the player when click on fullScreenIconView
        playerView?.player?.playWhenReady = false
        Log.d(TAG, "CLICK on media!")
    }

    override fun onLongClick(v: View?): Boolean {
        // When a long click happens, we fire our listener to get the media position in the list
        val callback: MediaAdapter.MediaLongClickListener? = callbackLongClickWeakRef?.get()
        callback?.onLongClickMedia(adapterPosition)
        // Pause the player when long click on fullScreenIconView
        playerView?.player?.playWhenReady = false
        Log.d(TAG, "LONG CLICK on media!")
        return true
    }

}
