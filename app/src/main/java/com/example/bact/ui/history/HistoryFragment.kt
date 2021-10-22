package com.example.bact.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bact.BACTApplication
import com.example.bact.R
import com.example.bact.adapter.ImageInfoListAdapter
import com.example.bact.databinding.FragmentHistoryBinding
import com.example.bact.model.database.ImageInfo

class HistoryFragment : Fragment() {

    companion object {
        const val TAG = "HistoryFragment"

        @JvmStatic
        fun newInstance() = HistoryFragment()
    }

    private val viewModel: HistoryFragmentViewModel by activityViewModels {
        HistoryFragmentViewModelFactory(BACTApplication.database.imageInfoDao())
    }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imageInfoListAdapter = ImageInfoListAdapter { view: View, imageInfo: ImageInfo ->
            showPopupMenu(view, imageInfo)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = imageInfoListAdapter
        }

        viewModel.allItems.observe(this.viewLifecycleOwner) {
            Log.d(TAG, "viewModel.allItems.observe")
            imageInfoListAdapter.setDataSourceList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPopupMenu(view: View, imageInfo: ImageInfo) {
        PopupMenu(activity, view).apply {
            menuInflater.inflate(R.menu.menu_item, this.menu)
            setOnMenuItemClickListener {
                viewModel.deleteItem(imageInfo)
                false
            }
            show()
        }
    }

}