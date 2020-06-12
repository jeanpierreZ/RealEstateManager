package com.openclassrooms.realestatemanager.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter
import com.openclassrooms.realestatemanager.models.RealEstateWithMedias
import com.openclassrooms.realestatemanager.views.viewmodels.RealEstateWithMediasViewModel
import kotlinx.android.synthetic.main.real_estate.view.*


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment(), RealEstateAdapter.Listener {

    companion object {
        private val TAG = ListFragment::class.java.simpleName
    }

    private var recyclerView: RecyclerView? = null
    private var realEstateAdapter: RealEstateAdapter? = null

    private lateinit var realEstateWithMediasViewModel: RealEstateWithMediasViewModel

    private var realEstateWithMediasList: MutableList<RealEstateWithMedias?> = mutableListOf()

    // Declare callback
    private var callbackRealEstate: OnRealEstateClickedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = inflater.inflate(R.layout.fragment_list, container, false)

        // Get RecyclerView from layout and serialise it
        recyclerView = fragmentView.findViewById(R.id.fragment_list_recycler_view)

        configureRecyclerView()

        // UpdateUI either with the search list or with the list of all properties
        val listFromSearch: ArrayList<RealEstateWithMedias>
        if (arguments != null && !requireArguments().isEmpty) {
            listFromSearch = arguments?.getParcelableArrayList<RealEstateWithMedias>(MainActivity.LIST_FROM_SEARCH)
                    as ArrayList<RealEstateWithMedias>
            updateUI(listFromSearch)
            arguments?.remove(MainActivity.LIST_FROM_SEARCH)
        } else {
            // Use the ViewModelProvider to associate the ViewModel with ListFragment
            realEstateWithMediasViewModel = ViewModelProvider(this).get(RealEstateWithMediasViewModel::class.java)

            // Get the itemWithPicturesList
            realEstateWithMediasViewModel.getRealEstateWithMedias.observe(viewLifecycleOwner, Observer { list ->
                Log.d(TAG, "list = $list")
                updateUI(list)
            })
        }

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of items
        realEstateAdapter = RealEstateAdapter(realEstateWithMediasList, Glide.with(this), this, requireActivity())
        // Attach the adapter to the recyclerView to populate items
        recyclerView?.adapter = realEstateAdapter
        // Set layout manager to position the items
        recyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    private fun updateUI(updateList: List<RealEstateWithMedias?>) {
        // Add the list from the request and notify the adapter
        realEstateAdapter?.setRealEstates(updateList)
        realEstateWithMediasList = updateList as MutableList<RealEstateWithMedias?>
    }

    //----------------------------------------------------------------------------------
    // Interface for callback from ItemAdapter

    override fun onClickRealEstate(position: Int) {
        // Save the item object in the RecyclerView
        val realEstateWithMedias: RealEstateWithMedias? = realEstateAdapter?.getPosition(position)
        // Spread the click to the parent activity with the item id
        callbackRealEstate?.onRealEstateClicked(realEstateWithMedias?.realEstate?.id)

        // Set accent light color for the real estate selected
        for (i in realEstateWithMediasList.indices) {
            if (i == position) {
                recyclerView?.get(i)?.real_estate_card_view?.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorAccentLight))
                recyclerView?.get(i)?.real_estate_price?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))
            } else {
                recyclerView?.get(i)?.real_estate_card_view?.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))
                recyclerView?.get(i)?.real_estate_price?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorAccentDark))
            }
        }

        Log.d(TAG, "Click on ${realEstateWithMedias?.realEstate?.id} ")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on a real estate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnRealEstateClickedListener {
        fun onRealEstateClicked(realEstateWithMediasId: Long?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackRealEstate = activity as OnRealEstateClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnRealEstateClickedListener")
        }
    }
}
