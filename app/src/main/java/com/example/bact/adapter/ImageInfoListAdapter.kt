package com.example.bact.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bact.databinding.HistoryRecyclerviewItemBinding
import com.example.bact.model.database.ImageInfo
import com.example.bact.util.FileIOUtil

class ImageInfoListAdapter :
    RecyclerView.Adapter<ImageInfoListAdapter.ViewHolder>() {

    private var dataSourceList = listOf<ImageInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = dataSourceList[position]
        holder.bind(current)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataSourceList(dataSourceList: List<ImageInfo>) {
        this.dataSourceList = dataSourceList
        notifyDataSetChanged()
    }


    class ViewHolder(private var binding: HistoryRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageInfo) {
            binding.apply {
                scaleTextView.text = "scale：${item.scale}"
                noiseGradeTextView.text = "deNoiseGrade：${item.noiseGrade}"
                dateTextView.text = item.date
                imageNameTextView.text = item.imageName
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSourceList.size
    }
}