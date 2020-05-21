package com.openclassrooms.realestatemanager.views.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.adapters.ItemAdapter
import com.openclassrooms.realestatemanager.databinding.ItemBinding
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.utils.MyUtils
import java.lang.ref.WeakReference


class ItemViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    // Represent an item (line) of a real estate property in the RecyclerView

    private val myUtils = MyUtils()

    // For data
    private var callbackWeakRef: WeakReference<ItemAdapter.Listener>? = null

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the item position in the list
        val callback: ItemAdapter.Listener? = callbackWeakRef?.get()
        callback?.onClickItem(adapterPosition)
    }

    fun updateItems(itemWithPictures: ItemWithPictures?, glide: RequestManager, callback: ItemAdapter.Listener) {
        // Update widgets
        binding.itemType.text = itemWithPictures?.item?.type
        binding.itemDistrict.text = itemWithPictures?.item?.itemAddress?.district
        myUtils.displayIntegerProperties(itemWithPictures?.item?.price, binding.itemPrice)

        if (itemWithPictures?.pictures?.size != 0) {
            binding.itemImage.let { glide.load(itemWithPictures?.pictures?.get(0)?.roomPicture).into(it) }
        }
        // Create a new weak Reference to our Listener
        callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }
}
