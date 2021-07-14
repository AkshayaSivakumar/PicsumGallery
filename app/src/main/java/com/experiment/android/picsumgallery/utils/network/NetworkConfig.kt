package com.experiment.android.picsumgallery.utils.network

import android.app.Application
import com.experiment.android.picsumgallery.utils.AppConstants
import java.io.File
import javax.inject.Inject

class NetworkConfig(private val context: Application) {
    /**
     * API Url
     */
    fun getBaseUrl(): String = AppConstants.BASE_URL

    fun getCacheDir(): File = context.cacheDir

    fun getCacheSize(): Long = 10 * 1024 * 1024

    fun getTimeoutSeconds(): Long = 60

}