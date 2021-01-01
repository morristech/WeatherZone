package com.soumik.weatherzone.utils

interface RequestCompleteListener<T> {
    fun onRequestCompleted(data: T)
    fun onRequestFailed(errorMessage: String?)
}
