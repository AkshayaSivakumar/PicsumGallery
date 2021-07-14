package com.experiment.android.picsumgallery.utils.network

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class ViewState<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): ViewState<T> =
            ViewState(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): ViewState<T> =
            ViewState(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): ViewState<T> =
            ViewState(status = Status.LOADING, data = data, message = null)
    }
}