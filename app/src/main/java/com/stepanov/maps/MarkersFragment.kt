package com.stepanov.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stepanov.maps.databinding.FragmentMarkersBinding


class MarkersFragment : Fragment() {

    private var _binding: FragmentMarkersBinding? = null //убрали утечку памяти
    private val binding: FragmentMarkersBinding
        get() {
            return _binding!!
        }
    private lateinit var viewModel: MarkerViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = viewModel.markers.count().toString()
    }

    fun setViewModel(viewModel: MarkerViewModel) {
        this.viewModel = viewModel
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MarkersFragment()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}