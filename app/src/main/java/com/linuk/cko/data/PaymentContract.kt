package com.linuk.cko.data

import com.google.gson.annotations.SerializedName
import com.linuk.cko.payment.PaymentUtils


data class CardDetails(
    val number: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String,
)

data class PaymentRequestBody(
    val number: String,
    @SerializedName("expiry_month")
    val expiryMonth: String,
    @SerializedName("expiry_year")
    val expiryYear: String,
    val cvv: String,
    @SerializedName("success_url")
    val successUrl: String,
    @SerializedName("failure_url")
    val failureUrl: String,
) {
    companion object {
        fun fromCardDetails(cardDetails: CardDetails) = PaymentRequestBody(
            number = cardDetails.number,
            expiryMonth = cardDetails.expiryMonth,
            expiryYear = cardDetails.expiryYear,
            cvv = cardDetails.cvv,
            successUrl = PaymentUtils.SUCCESS_PAYMENT_REDIRECTION_URL,
            failureUrl = PaymentUtils.FAILURE_PAYMENT_REDIRECTION_URL,
        )
    }
}

data class PaymentResponseBody(val url: String)
