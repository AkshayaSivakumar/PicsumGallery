package com.experiment.android.picsumgallery.utils.common

import com.experiment.android.picsumgallery.utils.AppConstants

fun Int.createImageUrl(width: Int, height: Int): String {
    return AppConstants.BASE_URL + "id/$this/$width/$height.jpg"
}