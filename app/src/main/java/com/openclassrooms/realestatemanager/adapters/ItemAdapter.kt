package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.databinding.ItemBinding
import com.openclassrooms.realestatemanager.models.ItemWithPictures
import com.openclassrooms.realestatemanager.views.viewholders.ItemViewHolder


class ItemAdapter(private var list: List<ItemWithPictures?>,
                  private val glide: RequestManager,
                  private val callback: Listener) : RecyclerView.Adapter<ItemViewHolder>() {

    // Callback
    interface Listener {
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
            holder.updateItems(list[position], glide, callback)

    fun setItems(itemWithPicturesList: List<ItemWithPictures?>) {
        list = itemWithPicturesList
        notifyDataSetChanged()
    }

    // Return the position of an item in the list
    fun getPosition(position: Int): ItemWithPictures? = list[position]
}
