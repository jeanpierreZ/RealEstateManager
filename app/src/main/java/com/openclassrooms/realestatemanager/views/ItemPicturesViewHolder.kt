package com.openclassrooms.realestatemanager.views

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import java.lang.ref.WeakReference

class ItemPicturesViewHolder(pictureView: View) : RecyclerView.ViewHolder(pictureView), View.OnClickListener {
    // Represent a picture of an item in the RecyclerView

    companion object {
        private val TAG = ItemPicturesViewHolder::class.java.simpleName
    }

    private var imagePicture: ImageView? = null

    // FOR DATA
    private var callbackWeakRef: WeakReference<ItemPicturesAdapter.PictureListener>? = null

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the picture position in the list
        val callback: ItemPicturesAdapter.PictureListener? = callbackWeakRef?.get()
        callback?.onClickPicture(adapterPosition)
        Log.d(TAG, "CLICK on picture !")
    }

    init {
        imagePicture = pictureView.findViewById(R.id.item_pictures_image)
    }

    fun updatePictures(picture: String?, glide: RequestManager, callback: ItemPicturesAdapter.PictureListener) {
        // Update widgets
        imagePicture?.let { glide.load(picture).into(it) }
        // Create a new weak Reference to our Listener
        this.callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }

}