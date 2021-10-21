package com.example.bact.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bact.BACTApplication
import com.example.bact.R
import com.example.bact.adapter.ImageInfoListAdapter
import com.example.bact.databinding.FragmentHistoryBinding
import com.example.bact.databinding.HistoryRecyclerviewItemBinding
import com.example.bact.model.database.ImageInfo

class HistoryFragment : Fragment() {

    companion object {
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
        var adapter = ImageInfoListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapter
        }

        viewModel.allItems.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}