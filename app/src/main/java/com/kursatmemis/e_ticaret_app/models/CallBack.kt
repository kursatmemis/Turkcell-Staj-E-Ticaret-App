package com.kursatmemis.e_ticaret_app.models

interface CallBack<T> {
    fun onSuccess(data: T)

    fun onFailure(errorMessage: String)
}