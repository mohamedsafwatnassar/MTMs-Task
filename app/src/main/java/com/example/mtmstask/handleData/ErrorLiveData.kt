package com.example.mtmstask.handleData

import androidx.lifecycle.MutableLiveData

class ErrorLiveData<T> : MutableLiveData<HandleError<T>>() {

    fun postConnectionError(throwable: String?) {
        postValue(HandleError<T>().connectionError(throwable!!))
    }

    fun postError(throwable: String?) {
        postValue(HandleError<T>().error(throwable!!))
    }

    fun postSuccess(data: T?) {
        postValue(HandleError<T>().success(data))
    }

}