package com.linuk.cko.payment

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class PaymentRepository {
    private val client by lazy { OkHttpClient() }

    fun makePayment(
        cardDetails: CardDetails,
        onSuccess: (redirectUrl: String) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        val request = buildRequest(PaymentRequestBody.fromCardDetails(cardDetails))
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.message?.let { onFailure(it) }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body != null) {
                    onSuccess(
                        Gson().fromJson(
                            response.body!!.string(),
                            PaymentResponseBody::class.java
                        ).url
                    )
                } else {
                    onFailure(response.message)
                    // TODO: handle failed response or null body
                }
            }
        })
    }

    companion object {
        private fun buildRequest(requestBody: PaymentRequestBody): Request {
            val jsonString = Gson().toJson(requestBody)
            return Request.Builder()
                .url("$BASE_URL/$PAYMENT_PATH")
                .header("Content-Type", "application/json")
                .post(jsonString.toRequestBody())
                .build()
        }

        private data class PaymentResponseBody(val url: String)

        private data class PaymentRequestBody(
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
                    successUrl = SUCCESS_PAYMENT_REDIRECTION_URL,
                    failureUrl = FAILURE_PAYMENT_REDIRECTION_URL,
                )
            }
        }
    }
}

data class CardDetails(
    val number: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String,
)
