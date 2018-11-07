package me.thuongle.googlebookssearch.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

data class Result<out T>(val data: T?, val status: NetworkStatus?) {

    val isLoading
        get() = this.status is LOADING
    val isError
        get() = this.status is ERROR
    val hasData
        get() = this.data != null

    override fun toString(): String {
        return when (this.status) {
            is LOADING -> "Loading[$data]"
            is LOADED -> "Loaded[$data]"
            is ERROR -> "Error[${status.message} - $data]"
            else -> "Unknown[$data]"
        }
    }

    companion object {
        fun <T> loading(data: T?): Result<T> {
            return Result(data, LOADING)
        }

        fun <T> success(data: T?): Result<T> {
            return Result(data, LOADED)
        }

        fun <T> error(message: String, throwable: Throwable?): Result<T> {
            return Result(null, ERROR(message, throwable))
        }
    }
}

sealed class NetworkStatus(val message: String = "", val error: Throwable? = null)

object LOADED : NetworkStatus()
object LOADING : NetworkStatus()
data class ERROR(val msg: String = "", val throwable: Throwable? = null) : NetworkStatus(msg, throwable)

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>