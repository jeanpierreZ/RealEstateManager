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
import com.openclassrooms.realestatemanager.adapters.ItemAdapter
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.views.ItemViewModel


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment(), ItemAdapter.Listener {

    private var recyclerView: RecyclerView? = null
    private var itemAdapter: ItemAdapter? = null

    private lateinit var itemViewModel: ItemViewModel

    private var itemList: MutableList<Item?> = mutableListOf()

    // Declare callback
    private var callbackItem: OnItemClickedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_list, container, false)

        // Get RecyclerView from layout and serialise it
        recyclerView = fragmentView.findViewById(R.id.fragment_list_recycler_view)

        configureRecyclerView()

        // Use the ViewModelProvider to associate the ViewModel with MainActivity
        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        itemViewModel.getItems.observe(viewLifecycleOwner, Observer { items ->
            // Update the cached copy of the words in the adapter.
            Log.d("LIST_FRAGMENT", "items = $items")
            items?.let { updateUI(it) }
        })

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Create the adapter by passing the list of items
        itemAdapter = ItemAdapter(itemList, Glide.with(this), this)
        // Attach the adapter to the recyclerView to populate items
        recyclerView?.adapter = itemAdapter
        // Set layout manager to position the items
        recyclerView?.layoutManager = LinearLayoutManager(activity)
    }

    private fun updateUI(updateList: List<Item?>) {
        // Add the list from the request and notify the adapter
        itemAdapter?.setItems(updateList)
    }

    //----------------------------------------------------------------------------------

    override fun onClickItem(position: Int) {
        // Save the item object in the RecyclerView
        val item: Item? = itemAdapter?.getPosition(position)
        // Spread the click to the parent activity
        callbackItem?.onItemClicked(item)
//        Log.d("LIST_FRAGMENT", "item.picture = ${item?.picture} ")
        Log.d("LIST_FRAGMENT", "item.type = ${item?.type} ")
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
        fun onItemClicked(item: Item?)
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
