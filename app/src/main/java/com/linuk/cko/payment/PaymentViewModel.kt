package com.linuk.cko.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaymentViewModel : ViewModel() {
    private val _viewType by lazy {
        MutableLiveData<ViewType>(ViewType.PaymentDetails)
    }

    val viewType: LiveData<ViewType>
        get() = _viewType

    fun onViewChanged(view: ViewType) {
        _viewType.postValue(view)
    }
}

sealed interface ViewType {
    object PaymentDetails : ViewType
    data class ThreeDS(val url: String) : ViewType
    data class PaymentResult(val isSuccessful: Boolean) : ViewType
}

enum class PaymentViewType(val view: String) {
    PAYMENT_DETAILS("PAYMENT_DETAILS"),
    THREE_DS("THREE_DS"),
    PAYMENT_RESULT("PAYMENT_RESULT")
}