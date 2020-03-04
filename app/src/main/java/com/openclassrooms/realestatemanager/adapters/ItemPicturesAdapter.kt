package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.views.ItemPicturesViewHolder


class ItemPicturesAdapter(private var list: ArrayList<String?>,
                          private val glide: RequestManager,
                          private val callback: PictureListener) : RecyclerView.Adapter<ItemPicturesViewHolder>() {

    // Callback
    interface PictureListener {
        fun onClickPicture(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPicturesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_picture, parent, false)
        return ItemPicturesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: ItemPicturesViewHolder, position: Int) {
        holder.updatePictures(this.list[position], this.glide, this.callback)
    }

    fun setPictures(pictureList: ArrayList<String?>) {
        this.list = pictureList
        notifyDataSetChanged()
    }

    // Return the position of a picture in the list
    fun getPosition(position: Int): String? {
        return this.list[position]
    }
}