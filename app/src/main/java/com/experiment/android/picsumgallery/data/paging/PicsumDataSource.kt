package com.experiment.android.picsumgallery.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.experiment.android.picsumgallery.data.remote.PicsumApi
import com.experiment.android.picsumgallery.model.PicsumResponse
import com.experiment.android.picsumgallery.utils.AppConstants.STARTING_PAGE_INDEX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PicsumDataSource @Inject constructor(private val picsumApi: PicsumApi) :
    PagingSource<Int, PicsumResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PicsumResponse> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        return withContext(Dispatchers.IO) {
            try {
                val response = picsumApi.getPicsumResponse(page = pageIndex)
                LoadResult.Page(
                    data = response,
                    prevKey = if (STARTING_PAGE_INDEX == pageIndex) null else pageIndex - 1,
                    nextKey = if (response.isEmpty()) null else pageIndex + 1
                )
            } catch (exception: IOException) {
                LoadResult.Error(exception)
            } catch (exception: HttpException) {
                LoadResult.Error(exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PicsumResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}