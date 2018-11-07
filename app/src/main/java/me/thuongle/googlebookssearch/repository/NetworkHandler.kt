package me.thuongle.googlebookssearch.repository

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import me.thuongle.googlebookssearch.model.Result
import me.thuongle.googlebookssearch.util.AppExecutors
import me.thuongle.googlebookssearch.util.postValueIfNew
import me.thuongle.googlebookssearch.util.setValueIfNew
import timber.log.Timber

abstract class NetworkHandler<ResultType>(appExecutors: AppExecutors) {

    val result = MutableLiveData<Result<ResultType>>()

    init {
        result.setValueIfNew(Result.loading(null))
        appExecutors.networkIO().execute {
            try {
                val data = execute()
                result.postValueIfNew(Result.success(data))
            } catch (e: Exception) {
                Timber.e(e.message, e)
                result.postValueIfNew(Result.error(e.message ?: "Unknown", e))
            }
        }
    }

    @WorkerThread
    protected abstract fun execute(): ResultType?
}