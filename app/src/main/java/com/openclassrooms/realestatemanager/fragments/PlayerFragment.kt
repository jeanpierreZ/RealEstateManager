package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.MediaAdapter
import com.openclassrooms.realestatemanager.models.Media


/**
 * A simple [Fragment] subclass.
 */
class PlayerFragment : Fragment(),
        MediaAdapter.MediaListener,
        MediaAdapter.MediaLongClickListener,
        MediaAdapter.MediaFullScreenListener {

    private var recyclerView: RecyclerView? = null
    private var mediaAdapter: MediaAdapter? = null
    private val isFullScreen = true
    private val isRealEstateActivity = false

    private var mediaList: ArrayList<Media?> = arrayListOf()
    private var mediaPosition: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_player, container, false)

        recyclerView = fragmentView.findViewById(R.id.player_fragment_recycler_view)

        mediaList = arguments?.getParcelableArrayList<Media>(DetailsFragment.BUNDLE_MEDIA_LIST) as ArrayList<Media?>
        mediaPosition = arguments?.getInt(DetailsFragment.BUNDLE_MEDIA_POSITION)

        // Add medias from detailsFragment
        updateMediaList(mediaList)
        configureRecyclerView()

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        // Hide status bar and toolbar
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        // Show status bar and toolbar
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters, LayoutManager & UI

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of media
        mediaAdapter = activity?.let {
            MediaAdapter(mediaList, Glide.with(this), this, this, this, isFullScreen, isRealEstateActivity, it)
        }
        // Attach the adapter to the recyclerView to populate medias
        recyclerView?.adapter = mediaAdapter
        // Set layout manager to position the medias
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Scroll to the media position in the list
        mediaPosition?.let { recyclerView?.smoothScrollToPosition(it) }

        // To swipe page after page
        recyclerView?.onFlingListener = null
        PagerSnapHelper().attachToRecyclerView(recyclerView)
    }

    private fun updateMediaList(updateList: ArrayList<Media?>) {
        mediaAdapter?.setMedias(updateList)
    }

    //----------------------------------------------------------------------------------
    // Interfaces for callback from MediaAdapter

    override fun onClickMedia(position: Int) {
        // Do nothing
    }

    override fun onLongClickMedia(position: Int) {
        // Do nothing
    }

    override fun onClickMediaFullScreen(position: Int) {
        activity?.onBackPressed()
    }

}
