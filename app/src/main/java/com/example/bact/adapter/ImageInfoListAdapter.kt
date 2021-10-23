package com.example.bact.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bact.BACTApplication
import com.example.bact.databinding.HistoryRecyclerviewItemBinding
import com.example.bact.model.database.ImageInfo

class ImageInfoListAdapter(private val onItemClicked: (View, ImageInfo) -> Unit) :
    RecyclerView.Adapter<ImageInfoListAdapter.ViewHolder>(){

    private var dataSourceList = listOf<ImageInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = dataSourceList[position]
        holder.bind(current)
        holder.itemView.setOnLongClickListener{
            Log.d("ImageInfoListAdapter","setOnLongClickListener")
            onItemClicked(holder.itemView,current)
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataSourceList(dataSourceList: List<ImageInfo>) {
        this.dataSourceList = dataSourceList
        notifyDataSetChanged()
    }


    class ViewHolder(private var binding: HistoryRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ImageInfo) {
            binding.apply {
                scaleTextView.text = "放大倍数：${item.scale}"
                noiseGradeTextView.text = "降噪程度：${BACTApplication.hashMap[item.noiseGrade]}"
                dateTextView.text = item.date
                imageNameTextView.text = item.imageName
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSourceList.size
    }

}