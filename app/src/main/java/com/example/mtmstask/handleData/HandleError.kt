package com.example.mtmstask.handleData

class HandleError<T> {

    private var status: DataStatus? = null

    private var data: T? = null

    private var connectionError: String? = null
    private var error: String? = null

    fun connectionError(error: String): HandleError<T> {
        status = DataStatus.CONNECTIONERROR
        this.connectionError = error
        this.error = null
        return this
    }

    fun error(error: String): HandleError<T> {
        status = DataStatus.ERROR
        this.error = error
        this.connectionError = null
        return this
    }

    fun success(data: T?): HandleError<T> {
        status = DataStatus.SUCCESS
        this.data = data
        this.connectionError = null
        this.error = null
        return this
    }

    fun getStatus(): DataStatus {
        return status!!
    }

    fun getError(): String? {
        return error
    }

    fun getConnectionError(): String? {
        return connectionError
    }

    fun getData(): T? {
        return data
    }

    enum class DataStatus {
        SUCCESS, CONNECTIONERROR, ERROR
    }
}