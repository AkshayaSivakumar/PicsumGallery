package com.experiment.android.picsumgallery.ui.base

import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.experiment.android.picsumgallery.R

fun ImageView.loadGalleryImage(imageUrl: String?, width: Int, height: Int) {

    if (null == imageUrl || "" == imageUrl) {
        Glide.with(this.context)
            .load(R.drawable.ic_broken_image)
            .centerCrop()
            .into(this)
    } else {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
            .load(imgUri)
            .optionalCenterCrop()
            .override(width, height)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_loading_image)
            .error(R.drawable.ic_broken_image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

fun ImageView.loadDetailImage(imageUrl: String?, width: Int, height: Int) {

    if (null == imageUrl || "" == imageUrl) {
        Glide.with(this.context)
            .load(R.drawable.ic_broken_image_1)
            .centerCrop()
            .into(this)
    } else {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
            .load(imgUri)
            .dontAnimate()
            .fitCenter()
            .override(width, height)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_broken_image_1)
            .into(this)
    }
}