package com.openclassrooms.realestatemanager.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.databinding.FragmentPlayerBinding

/**
 * A simple [Fragment] subclass.
 */
class PlayerFragment : Fragment() {

    // View binding
    private var _binding: FragmentPlayerBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        val mediaToPlay: Uri? = arguments?.getParcelable(MainActivity.MEDIA_TO_PLAY)
        val isMediaVideo = arguments?.getBoolean(MainActivity.IS_MEDIA_VIDEO)

        if (isMediaVideo != null && isMediaVideo) {
            binding.fragmentPlayerImage.visibility = View.GONE
            binding.fragmentPlayerVideo.setVideoURI(mediaToPlay)
            binding.fragmentPlayerVideo.start()
            val mediaController = MediaController(activity)
            binding.fragmentPlayerVideo.setMediaController(mediaController)
            mediaController.setAnchorView(binding.fragmentPlayerVideo)
        } else {
            binding.fragmentPlayerVideo.visibility = View.GONE
            Glide.with(this).load(mediaToPlay).into(binding.fragmentPlayerImage)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
