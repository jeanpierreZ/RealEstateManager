package com.openclassrooms.realestatemanager.views.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.databinding.ItemPictureBinding
import com.openclassrooms.realestatemanager.models.Picture
import java.lang.ref.WeakReference

class ItemPicturesViewHolder(val binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {
    // Represent a picture of a real estate property in the RecyclerView

    companion object {
        private val TAG = ItemPicturesViewHolder::class.java.simpleName
    }

    // For data
    private var callbackWeakRef: WeakReference<ItemPicturesAdapter.PictureListener>? = null
    private var callbackLongClickWeakRef: WeakReference<ItemPicturesAdapter.PictureLongClickListener>? = null

    fun updatePictures(picture: Picture?, glide: RequestManager,
                       callback: ItemPicturesAdapter.PictureListener,
                       callbackLongClick: ItemPicturesAdapter.PictureLongClickListener) {
        // Update widgets
        binding.itemPictureText.text = picture?.pictureDescription
        binding.itemPictureImage.let { glide.load(picture?.roomPicture).into(it) }

        // Create news weak References to our Listeners and implement listeners

        this.callbackWeakRef = WeakReference(callback)
        itemView.setOnClickListener(this)

        this.callbackLongClickWeakRef = WeakReference(callbackLongClick)
        itemView.setOnLongClickListener(this)
    }

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the picture position in the list
        val callback: ItemPicturesAdapter.PictureListener? = callbackWeakRef?.get()
        callback?.onClickPicture(adapterPosition)
        Log.d(TAG, "CLICK on picture !")
    }

    override fun onLongClick(v: View?): Boolean {
        // When a long click happens, we fire our listener to get the picture position in the list
        val callback: ItemPicturesAdapter.PictureLongClickListener? = callbackLongClickWeakRef?.get()
        callback?.onLongClickItem(adapterPosition)
        Log.d(TAG, "LONG CLICK on picture !")
        return true
    }
}
