package com.experiment.android.picsumgallery.ui.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.experiment.android.picsumgallery.R
import com.experiment.android.picsumgallery.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)

        binding.lifecycleOwner = viewLifecycleOwner
    }
}