package me.thuongle.googlebookssearch.repository

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import me.thuongle.googlebookssearch.model.Result
import me.thuongle.googlebookssearch.util.AppExecutors
import me.thuongle.googlebookssearch.util.postValueIfNew
import me.thuongle.googlebookssearch.util.setValueIfNew
import timber.log.Timber
import java.io.IOException

abstract class NetworkHandler<ResultType>(appExecutors: AppExecutors) {

    val result = MutableLiveData<Result<ResultType>>()

    init {
        result.setValueIfNew(Result.loading(null))
        appExecutors.networkIO().execute {
            try {
                val data = requestService()
                result.postValueIfNew(Result.success(data))
            } catch (e: IOException) {
                Timber.e(e.message, e)
                result.postValueIfNew(Result.error(e.message ?: "Unknown", e))
            }
        }
    }

    @WorkerThread
    protected abstract fun requestService(): ResultType?
}