package com.linuk.cko.api

import com.linuk.cko.data.CardDetails

interface PaymentRepository {
    fun makePayment(
        cardDetails: CardDetails,
        onSuccess: (redirectUrl: String) -> Unit,
        onFailure: (message: String) -> Unit
    )
}