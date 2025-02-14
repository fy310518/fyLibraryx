package com.fy.baselibrary.retrofit.observer

import java.io.File

sealed class TransmissionState {

    data class InProgress(val progress: Float) : TransmissionState()
    data class Success(val file: File) : TransmissionState()
    data class Error(val throwable: Throwable) : TransmissionState()

}