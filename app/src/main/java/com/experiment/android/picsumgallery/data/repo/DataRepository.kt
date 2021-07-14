package com.experiment.android.picsumgallery.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.experiment.android.picsumgallery.data.paging.PicsumDataSource
import com.experiment.android.picsumgallery.data.remote.PicsumApi
import com.experiment.android.picsumgallery.model.PicsumResponse
import com.experiment.android.picsumgallery.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val picsumApi: PicsumApi
) {
    suspend fun getPhotosList(): Flow<PagingData<PicsumResponse>> {

        return Pager(
            config = PagingConfig(
                pageSize = AppConstants.PAGE_SIZE,
                prefetchDistance = 5,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PicsumDataSource(picsumApi)
            }
        ).flow
    }
}