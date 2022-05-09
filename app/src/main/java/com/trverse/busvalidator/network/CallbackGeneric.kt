package com.trverse.busvalidator.network;

interface CallbackGeneric<T> {
    fun onResult(response: T, requestCode: Int)
    fun onError(errorMessage: String, requestCode: Int)
}