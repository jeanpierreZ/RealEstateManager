package com.openclassrooms.realestatemanager.views

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemAdapter
import com.openclassrooms.realestatemanager.models.Item
import java.lang.ref.WeakReference


class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    // Represent an item (line) of a real estate property in the RecyclerView

    companion object {
        private val TAG = ItemViewHolder::class.java.simpleName
    }

    private var textViewType: TextView? = null
    private var textViewDistrict: TextView? = null
    private var textViewPrice: TextView? = null
    private var imageItem: ImageView? = null

    // FOR DATA
    private var callbackWeakRef: WeakReference<ItemAdapter.Listener>? = null

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the item position in the list
        val callback: ItemAdapter.Listener? = callbackWeakRef?.get()
        callback?.onClickItem(adapterPosition)
        Log.d(TAG, "CLICK!")
    }

    init {
        textViewType = itemView.findViewById(R.id.item_type)
        textViewDistrict = itemView.findViewById(R.id.item_district)
        textViewPrice = itemView.findViewById(R.id.item_price)
        imageItem = itemView.findViewById(R.id.item_image)
    }

    fun updateItems(item: Item?, glide: RequestManager, callback: ItemAdapter.Listener) {
        // Update widgets
        textViewType?.text = item?.type
        textViewDistrict?.text = item?.district
        textViewPrice?.text = item?.price.toString()
        imageItem?.let { glide.load(item?.photo?.get(1)).into(it) }
        // Create a new weak Reference to our Listener
        this.callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }
}