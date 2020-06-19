package com.openclassrooms.realestatemanager.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.toolbar.*


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

    private var isTablet = false
    private var searchResult = false

    // Declare callback
    private var callbackRealEstate: OnRealEstateClickedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = inflater.inflate(R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)

        // Get RecyclerView from layout and serialise it
        recyclerView = fragmentView.findViewById(R.id.fragment_list_recycler_view)

        // Boolean to accentuate or not the color of the real estate
        isTablet = arguments?.getBoolean(MainActivity.IS_TABLET)!!

        configureRecyclerView()

        // UpdateUI either with the search list or with the list of all properties
        val listFromSearch: ArrayList<RealEstateWithMedias>
        if (arguments?.getParcelableArrayList<RealEstateWithMedias>(MainActivity.LIST_FROM_SEARCH) != null) {
            listFromSearch = arguments?.getParcelableArrayList<RealEstateWithMedias>(MainActivity.LIST_FROM_SEARCH)
                    as ArrayList<RealEstateWithMedias>
            updateUI(listFromSearch)
            arguments?.remove(MainActivity.LIST_FROM_SEARCH)
            searchResult = true
        } else {
            // Use the ViewModelProvider to associate the ViewModel with ListFragment
            realEstateWithMediasViewModel = ViewModelProvider(this).get(RealEstateWithMediasViewModel::class.java)

            // Get the itemWithPicturesList
            realEstateWithMediasViewModel.getRealEstateWithMedias.observe(viewLifecycleOwner, Observer { list ->
                Log.d(TAG, "list = $list")
                updateUI(list)
            })
            searchResult = false
        }

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Method for Toolbar Menu

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (searchResult) {
            menu.findItem(R.id.menu_toolbar_add)?.isVisible = false
            activity?.toolbar?.setTitle(R.string.search_results)
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.toolbar?.setTitle(R.string.app_name)
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of items
        realEstateAdapter = RealEstateAdapter(realEstateWithMediasList, Glide.with(this), this, isTablet, requireActivity())
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
    // Interface for callback from RealEstateAdapter

    override fun onClickRealEstate(position: Int) {
        // Save the item object in the RecyclerView
        val realEstateWithMedias: RealEstateWithMedias? = realEstateAdapter?.getPosition(position)
        // Spread the click to the parent activity with the item id
        callbackRealEstate?.onRealEstateClicked(realEstateWithMedias?.realEstate?.id)
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
