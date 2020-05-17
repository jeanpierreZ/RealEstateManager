package com.openclassrooms.realestatemanager.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.openclassrooms.realestatemanager.adapters.ItemAdapter
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.views.viewmodels.ItemWithPicturesViewModel


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment(), ItemAdapter.Listener {

    companion object {
        private val TAG = ListFragment::class.java.simpleName
    }

    private var recyclerView: RecyclerView? = null
    private var itemAdapter: ItemAdapter? = null

    private lateinit var itemWithPicturesViewModel: ItemWithPicturesViewModel

    private var itemWithPicturesList: MutableList<ItemWithPictures?> = mutableListOf()

    // Declare callback
    private var callbackItem: OnItemClickedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_list, container, false)

        // Get RecyclerView from layout and serialise it
        recyclerView = fragmentView.findViewById(R.id.fragment_list_recycler_view)

        configureRecyclerView()

        // UpdateUI either with the search list or with the list of all properties
        val listFromSearch: ArrayList<ItemWithPictures>
        if (arguments != null && !requireArguments().isEmpty) {
            listFromSearch = arguments?.getParcelableArrayList<ItemWithPictures>(MainActivity.LIST_FROM_SEARCH)
                    as ArrayList<ItemWithPictures>
            Log.w(TAG, "listFromSearch = $listFromSearch")
            updateUI(listFromSearch)
            arguments?.remove(MainActivity.LIST_FROM_SEARCH)
        } else {
            // Use the ViewModelProvider to associate the ViewModel with ListFragment
            itemWithPicturesViewModel = ViewModelProvider(this).get(ItemWithPicturesViewModel::class.java)

            // Get the itemWithPicturesList
            itemWithPicturesViewModel.getItemWithPictures.observe(viewLifecycleOwner, Observer { itemWithPicturesList ->
                Log.d(TAG, "itemWithPicturesList = $itemWithPicturesList")
                updateUI(itemWithPicturesList)
            })
        }

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of items
        itemAdapter = ItemAdapter(itemWithPicturesList, Glide.with(this), this)
        // Attach the adapter to the recyclerView to populate items
        recyclerView?.adapter = itemAdapter
        // Set layout manager to position the items
        recyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    private fun updateUI(updateList: List<ItemWithPictures?>) {
        // Add the list from the request and notify the adapter
        itemAdapter?.setItems(updateList)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback from ItemAdapter

    override fun onClickItem(position: Int) {
        // Save the item object in the RecyclerView
        val itemWithPictures: ItemWithPictures? = itemAdapter?.getPosition(position)
        // Spread the click to the parent activity with the item id
        callbackItem?.onItemClicked(itemWithPictures?.item?.id)
        Log.d(TAG, "Click on ${itemWithPictures?.item?.type} ")
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on an item

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnItemClickedListener {
        fun onItemClicked(itemWithPicturesId: Long?)
    }

    // Create callback to parent activity
    private fun createCallbackToParentActivity() {
        try {
            // Parent activity will automatically subscribe to callback
            callbackItem = activity as OnItemClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$e must implement OnItemClickedListener")
        }
    }
}
