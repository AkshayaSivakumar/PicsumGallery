package com.experiment.android.picsumgallery.ui.gallery

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.experiment.android.picsumgallery.data.repo.DataRepository
import com.experiment.android.picsumgallery.model.PicsumResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PicsumGalleryViewModel @Inject constructor(
    application: Application,
    private val repository: DataRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _navigateToDetails = MutableLiveData<PicsumResponse>()
    val navigateToDetails: LiveData<PicsumResponse>
        get() = _navigateToDetails

    suspend fun getPhotosList() = repository.getPhotosList().cachedIn(viewModelScope)

    fun navigateToDetailsFragment(picsumResponse: PicsumResponse) {
        _navigateToDetails.value = picsumResponse
    }

    fun navigateToDetailsFragmentComplete() {
        _navigateToDetails.value = null
    }
}