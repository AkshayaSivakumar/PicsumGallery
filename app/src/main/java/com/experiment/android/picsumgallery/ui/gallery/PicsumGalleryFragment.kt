package com.experiment.android.picsumgallery.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.experiment.android.picsumgallery.R
import com.experiment.android.picsumgallery.databinding.FragmentPicsumGalleryBinding
import com.experiment.android.picsumgallery.utils.customcomponents.CustomItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PicsumGalleryFragment : Fragment(R.layout.fragment_picsum_gallery) {

    private val galleryViewModel by viewModels<PicsumGalleryViewModel>()

    private var _binding: FragmentPicsumGalleryBinding? = null
    private val binding get() = _binding!!

    private var coroutineJob: Job? = null

    private lateinit var galleryAdapter: PicsumGalleryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPicsumGalleryBinding.bind(view)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = galleryViewModel

        initRecyclerView()
        loadData()
        observeToNavigate()

        setHasOptionsMenu(true)
    }

    private fun initRecyclerView() {
        galleryAdapter =
            PicsumGalleryAdapter(PicsumGalleryAdapter.ListItemClickListener {
                galleryViewModel.navigateToDetailsFragment(it)
            })

        binding.rcvLaunchList.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(CustomItemDecorator(20, 15))
            this.adapter = galleryAdapter.withLoadStateHeaderAndFooter(
                header = PicsumLoadStateAdapter { galleryAdapter.retry() },
                footer = PicsumLoadStateAdapter { galleryAdapter.retry() },
            )

            binding.layoutError.btnRetry.setOnClickListener {
                galleryAdapter.retry()
            }
        }

    }

    private fun loadData() {
        coroutineJob?.cancel()
        coroutineJob = lifecycleScope.launch {
            galleryViewModel.getPhotosList().collectLatest {
                it.let {
                    galleryAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }

        /**
         * Set recycler view and error layout visibilities based on the LoadingState
         */
        viewLifecycleOwner.lifecycleScope.launch {
            galleryAdapter.loadStateFlow.collectLatest { loadState ->
                binding.apply {
                    //Show progress bar when the state is loading
                    layoutError.containerCustomProgress.isVisible =
                        loadState.refresh is LoadState.Loading
                    //Show recycler view if the state when not loading
                    rcvLaunchList.isVisible = loadState.refresh is LoadState.NotLoading
                    //Show retry button if the status is error
                    layoutError.btnRetry.isVisible = loadState.refresh is LoadState.Error
                    //Show no network image view when the load state is error
                    layoutError.ivNoNetwork.isVisible = loadState.refresh is LoadState.Error
                    //Show msg/error textview when the status is loading or error and show the appropriate message
                    layoutError.tvMsgError.isVisible =
                        loadState.refresh is LoadState.Loading || loadState.refresh is LoadState.Error

                    if (loadState.refresh is LoadState.Loading) {
                        layoutError.tvMsgError.text = resources.getString(R.string.loading)
                    } else if (loadState.refresh is LoadState.Error) {
                        layoutError.tvMsgError.text = resources.getString(R.string.no_network_error)
                    }

                    if (loadState.source.refresh is LoadState.NotLoading
                        && loadState.append.endOfPaginationReached
                        && galleryAdapter.itemCount < 1
                    ) {
                        rcvLaunchList.isVisible = false
                        layoutError.tvEmptyError.isVisible = true
                    } else {
                        layoutError.tvEmptyError.isVisible = false
                    }
                }
            }
        }
    }

    private fun observeToNavigate() {
        /**
         * Observe the recyclerview item click
         */
        galleryViewModel.navigateToDetails.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    PicsumGalleryFragmentDirections.actionPicsumGalleryFragmentToDetailsFragment(it)
                )
                galleryViewModel.navigateToDetailsFragmentComplete()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

}