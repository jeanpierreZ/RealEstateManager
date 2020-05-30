package com.openclassrooms.realestatemanager.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import kotlinx.android.synthetic.main.fragment_player.*


/**
 * A simple [Fragment] subclass.
 */
class PlayerFragment : Fragment() {

    private lateinit var playerVideo: VideoView
    private lateinit var playerImage: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_player, container, false)

        playerVideo = fragmentView.findViewById(R.id.fragment_player_video)
        playerImage = fragmentView.findViewById(R.id.fragment_player_image)

        val mediaToPlay: Uri? = arguments?.getParcelable(DetailsFragment.BUNDLE_MEDIA_TO_PLAY)
        val isMediaVideo = arguments?.getBoolean(DetailsFragment.BUNDLE_IS_MEDIA_VIDEO)

        if (isMediaVideo != null && isMediaVideo) {
            playerImage.visibility = View.GONE
            playerVideo.setVideoURI(mediaToPlay)
            playerVideo.start()
            val mediaController = MediaController(activity)
            playerVideo.setMediaController(mediaController)
            mediaController.setAnchorView(playerVideo)
        } else {
            playerVideo.visibility = View.GONE
            Glide.with(this).load(mediaToPlay).into(playerImage)
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

}
