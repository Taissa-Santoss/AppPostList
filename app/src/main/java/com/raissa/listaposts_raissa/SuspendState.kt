package com.raissa.listaposts_raissa;

sealed class SuspendState<out SuccesDataType>{

data class Success<out T>(val data: T): SuspendState<T>()

data class Error(val message: String): SuspendState<Nothing>()

data object Loading: SuspendState<Nothing>()
}

