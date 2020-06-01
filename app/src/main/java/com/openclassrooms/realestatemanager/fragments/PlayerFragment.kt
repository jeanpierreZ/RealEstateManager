package com.openclassrooms.realestatemanager.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import kotlinx.android.synthetic.main.fragment_player.view.*


/**
 * A simple [Fragment] subclass.
 */
class PlayerFragment : Fragment() {

    private var player: SimpleExoPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_player, container, false)

        val mediaToPlay: Uri? = arguments?.getParcelable(DetailsFragment.BUNDLE_MEDIA_TO_PLAY)
        val isMediaVideo = arguments?.getBoolean(DetailsFragment.BUNDLE_IS_MEDIA_VIDEO)

        // Build SimpleExoPlayer
        player = activity?.let { SimpleExoPlayer.Builder(it).build() }
        // Bind the player to the view.
        fragmentView.fragment_player_video.player = player

        if (isMediaVideo != null && isMediaVideo) {
            fragmentView.fragment_player_image.visibility = View.GONE

            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                    activity?.let { Util.getUserAgent(it, BuildConfig.APPLICATION_ID) })
            // This is the MediaSource representing the media to be played.
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaToPlay)
            // Prepare the player with the source.
            player?.prepare(videoSource)

        } else {
            fragmentView.fragment_player_video.visibility = View.GONE
            Glide.with(this).load(mediaToPlay).into(fragmentView.fragment_player_image)
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
    }
}
