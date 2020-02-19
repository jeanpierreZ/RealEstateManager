package com.openclassrooms.realestatemanager.fragments


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
            "description", listOf("photo"), "address", "district",
            listOf("poi"), "status", "01/01/01", "02/02/02", "bobby")

    private var secondItemTest: Item = Item(0, "type2", 200, 10, 2,
            "description", listOf("photo"), "address", "district2",
            listOf("poi"), "status", "01/01/01", "02/02/02", "bobby")

    private var itemList: MutableList<Item?> = mutableListOf()


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

    override fun onClickItem(position: Int) {
    }

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
}
