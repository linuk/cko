package com.linuk.cko.data

import com.google.gson.Gson
import com.linuk.cko.api.PaymentRepository
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.BASE_URL
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.PAYMENT_PATH
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    private val client by lazy { OkHttpClient() }

    override fun makePayment(
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
    }
}
