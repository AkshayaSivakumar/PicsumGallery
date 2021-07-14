package com.experiment.android.picsumgallery.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.experiment.android.picsumgallery.databinding.ItemImagesBinding
import com.experiment.android.picsumgallery.model.PicsumResponse
import com.experiment.android.picsumgallery.ui.base.loadGalleryImage
import com.experiment.android.picsumgallery.utils.AppConstants

class PicsumGalleryAdapter(val itemClickListener: ListItemClickListener) :
    PagingDataAdapter<PicsumResponse, PicsumGalleryAdapter.GalleryViewHolder>(
        DIFF_UTIL
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding =
            ItemImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (null != currentItem)
            holder.bind(currentItem)
    }

    inner class GalleryViewHolder(private val binding: ItemImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: PicsumResponse) {
            binding.model = model
            binding.ivImage.loadGalleryImage(
                model.downloadUrl,
                AppConstants.IMAGE_WIDTH_LANDING,
                AppConstants.IMAGE_HEIGHT_LANDING
            )
            binding.cardView.setOnClickListener {
                itemClickListener.itemClicked(model)
            }
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PicsumResponse>() {
            override fun areItemsTheSame(
                oldItem: PicsumResponse,
                newItem: PicsumResponse
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PicsumResponse,
                newItem: PicsumResponse
            ): Boolean =
                oldItem == newItem
        }
    }

    class ListItemClickListener(val itemClickListener: (pastLaunchData: PicsumResponse) -> Unit) {
        fun itemClicked(imageDetailsData: PicsumResponse) = itemClickListener(imageDetailsData)
    }
}