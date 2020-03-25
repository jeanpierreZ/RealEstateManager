package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.views.viewholders.ItemPicturesViewHolder


class ItemPicturesAdapter(private var list: ArrayList<Picture?>,
                          private val glide: RequestManager,
                          private val callback: PictureListener,
                          private val callbackLongClick: PictureLongClickListener)
    : RecyclerView.Adapter<ItemPicturesViewHolder>() {

    //----------------------------------------------------------------------------------
    // Callbacks

    interface PictureListener {
        fun onClickPicture(position: Int)
    }

    interface PictureLongClickListener {
        fun onLongClickItem(position: Int)
    }

    //----------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPicturesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_picture, parent, false)
        return ItemPicturesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    override fun onBindViewHolder(holder: ItemPicturesViewHolder, position: Int) {
        holder.updatePictures(this.list[position], this.glide, this.callback, this.callbackLongClick)
    }

    fun setPictures(pictureList: ArrayList<Picture?>) {
        this.list = pictureList
        notifyDataSetChanged()
    }

    // Return the position of a picture in the list
    fun getPosition(position: Int): Picture? {
        return this.list[position]
    }
}