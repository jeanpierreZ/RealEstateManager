package com.openclassrooms.realestatemanager.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemAdapter
import com.openclassrooms.realestatemanager.models.Item


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment(), ItemAdapter.Listener {

    private var recyclerView: RecyclerView? = null
    private var itemAdapter: ItemAdapter? = null

    private var itemTest: Item = Item(0, "type", 100, 10, 2,
            "description", arrayListOf("https://cdn.pixabay.com/photo/2016/03/04/03/54/catherine-deneuve-1235443__340.jpg",
            "https://cdn.pixabay.com/photo/2016/03/20/17/19/marylyn-monroe-female-1269011__340.jpg"), "address", "district",
            arrayListOf("poi"), "status", "01/01/01", "02/02/02", "bobby")

    private var secondItemTest: Item = Item(0, "type2", 200, 10, 2,
            "description", arrayListOf("https://cdn.pixabay.com/photo/2019/08/09/21/20/james-dean-4395893_960_720.jpg",
            "https://cdn.pixabay.com/photo/2019/08/17/13/51/clint-eastwood-4412219__340.jpg"), "address", "district2",
            arrayListOf("poi"), "status", "01/01/01", "02/02/02", "bobby")

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

        itemList.add(0, itemTest)
        itemList.add(1, secondItemTest)
        updateUI(itemList)

        return fragmentView
    }

    //----------------------------------------------------------------------------------
    // Configure RecyclerViews, Adapters & LayoutManager

    private fun configureRecyclerView() {
        // Reset list
        //itemList.clear()
        // Create the adapter by passing the list of articles
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
        // Spread the click to the parent activity with the position of the item in the RecyclerView
        callbackItem?.onItemClicked(position)
    }

    //----------------------------------------------------------------------------------
    // Interface for callback to parent activity and associated methods when click on an article

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Call the method that creating callback after being attached to parent activity
        createCallbackToParentActivity()
    }

    // Declare our interface that will be implemented by any container activity
    interface OnItemClickedListener {
        fun onItemClicked(position: Int)
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
