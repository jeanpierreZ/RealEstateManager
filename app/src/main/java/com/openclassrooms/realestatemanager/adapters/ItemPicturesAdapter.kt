package com.openclassrooms.realestatemanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.databinding.ItemPictureBinding
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
        val binding = ItemPictureBinding.inflate(inflater, parent, false)
        return ItemPicturesViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemPicturesViewHolder, position: Int) =
            holder.updatePictures(list[position], glide, callback, callbackLongClick)

    fun setPictures(pictureList: ArrayList<Picture?>) {
        list = pictureList
        notifyDataSetChanged()
    }

    // Return the position of a picture in the list
    fun getPosition(position: Int): Picture? = list[position]
}
