package com.openclassrooms.realestatemanager.views.viewholders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ItemPicturesAdapter
import com.openclassrooms.realestatemanager.models.Picture
import java.lang.ref.WeakReference

class ItemPicturesViewHolder(pictureView: View) : RecyclerView.ViewHolder(pictureView), View.OnClickListener {
    // Represent a picture of an item in the RecyclerView

    companion object {
        private val TAG = ItemPicturesViewHolder::class.java.simpleName
    }

    private var imagePicture: ImageView? = null
    private var textPicture: TextView? = null

    // FOR DATA
    private var callbackWeakRef: WeakReference<ItemPicturesAdapter.PictureListener>? = null

    override fun onClick(view: View?) {
        // When a click happens, we fire our listener to get the picture position in the list
        val callback: ItemPicturesAdapter.PictureListener? = callbackWeakRef?.get()
        callback?.onClickPicture(adapterPosition)
        Log.d(TAG, "CLICK on picture !")
    }

    init {
        imagePicture = pictureView.findViewById(R.id.item_picture_image)
        textPicture = pictureView.findViewById(R.id.item_picture_text)
    }

    fun updatePictures(picture: Picture?, glide: RequestManager, callback: ItemPicturesAdapter.PictureListener) {
        // Update widgets
        textPicture?.text = picture?.pictureLocation
        imagePicture?.let { glide.load(picture?.roomPicture).into(it) }
        // Create a new weak Reference to our Listener
        this.callbackWeakRef = WeakReference(callback)
        // Implement Listener
        itemView.setOnClickListener(this)
    }

}