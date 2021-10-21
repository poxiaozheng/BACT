package com.example.bact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bact.databinding.HistoryRecyclerviewItemBinding
import com.example.bact.model.database.ImageInfo
import com.example.bact.util.FileIOUtil

class ImageInfoListAdapter() :
    ListAdapter<ImageInfo, ImageInfoListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }


    class ViewHolder(private var binding: HistoryRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageInfo) {
            binding.apply {
                imageView.setImageBitmap(FileIOUtil.byteArrayToBitmap(item.imageByteArray, null))
                scaleTextView.text = item.scale.toString()
                noiseGradeTextView.text = item.noiseGrade.toString()
                dateTextView.text = item.date
                imageNameTextView.text = item.imageName
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ImageInfo>() {
            override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
                return oldItem.imageName == newItem.imageName
            }
        }
    }
}