package com.openclassrooms.realestatemanager.views.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
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
import com.openclassrooms.realestatemanager.utils.ScaleViewListener
import java.lang.ref.WeakReference


class MediaFullScreenViewHolder(mediaFullScreenView: View) : RecyclerView.ViewHolder(mediaFullScreenView),
        View.OnClickListener {
    // Represent a real estate media in full screen in the RecyclerView

    companion object {
        private val TAG = MediaFullScreenViewHolder::class.java.simpleName
    }

    private var imageView: ImageView? = null
    private var closePictureView: ImageView? = null
    private var playerView: PlayerView? = null
    private var fullScreenIconView: ImageView? = null

    // For data
    private var callbackWeakRef: WeakReference<MediaAdapter.MediaFullScreenListener>? = null

    init {
        imageView = mediaFullScreenView.findViewById(R.id.media_full_screen_image)
        closePictureView = mediaFullScreenView.findViewById(R.id.media_full_screen_close)
        playerView = mediaFullScreenView.findViewById(R.id.media_full_screen_player_video)
        fullScreenIconView = mediaFullScreenView.findViewById(R.id.exo_fullscreen)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun updateMediasFullScreen(media: Media?, glide: RequestManager,
                               callback: MediaAdapter.MediaFullScreenListener, context: Context) {

        // Video
        if (media?.mediaVideo != null && media.mediaVideo?.isAbsolute!!) {
            imageView?.visibility = View.GONE
            closePictureView?.visibility = View.GONE
            fullScreenIconView?.background = ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_exit_white_24dp)

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
            // Use the listener for zoom the imageView
            imageView?.setOnTouchListener(ScaleViewListener(context))
        }

        // Create news weak References to our Listener and implement listener
        callbackWeakRef = WeakReference(callback)
        closePictureView?.setOnClickListener(this)
        fullScreenIconView?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the media position in the list
        val callback: MediaAdapter.MediaFullScreenListener? = callbackWeakRef?.get()
        callback?.onClickMediaFullScreen(adapterPosition)
        Log.d(TAG, "CLICK on full screen media!")
    }

}
