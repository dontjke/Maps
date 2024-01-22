package com.stepanov.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stepanov.maps.databinding.FragmentMapsBinding


class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null //убрали утечку памяти
    private val binding: FragmentMapsBinding
        get() {
            return _binding!!
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapsFragment()
    }
}