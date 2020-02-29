package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Item
import com.openclassrooms.realestatemanager.views.ItemViewHolder


class ItemAdapter(private var list: List<Item?>,
                  private val glide: RequestManager,
                  private val callback: Listener) : RecyclerView.Adapter<ItemViewHolder>() {

    // Callback
    interface Listener {
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.updateItems(this.list[position], this.glide, this.callback)
    }

    fun setItems(itemList: List<Item?>) {
        this.list = itemList
        notifyDataSetChanged()
    }

    // Return the position of an item in the list
    fun getPosition(position: Int): Item? {
        return this.list[position]
    }
}