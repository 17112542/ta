package com.mobile.ta.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.utils.wrapper.status.StatusType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected fun launchViewModelScope(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            block.invoke()
        }
    }

    protected fun checkStatus(
        status: StatusType,
        onSuccessListener: () -> Unit,
        onFailureListener: (() -> Unit)? = null
    ) {
        if (status == StatusType.FAILED) {
            onFailureListener?.invoke()
        } else {
            onSuccessListener.invoke()
        }
    }

    protected fun <T> checkStatus(
        status: Status<T>,
        onSuccessListener: (T) -> Unit,
        onFailureListener: () -> Unit
    ) {
        if (status.status == StatusType.FAILED || status.data == null) {
            onFailureListener.invoke()
        } else {
            onSuccessListener.invoke(status.data)
        }
    }
}