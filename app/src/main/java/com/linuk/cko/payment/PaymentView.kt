package com.linuk.cko.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.linuk.cko.payment.ViewType.PaymentDetails
import com.linuk.cko.payment.ViewType.PaymentResult
import com.linuk.cko.payment.ViewType.ThreeDS

@Composable
fun PaymentView(viewModel: PaymentViewModel) {
    val viewType by viewModel.viewType.observeAsState()

    when (viewType) {
        PaymentDetails -> PaymentDetailsView(viewModel)
        is PaymentResult -> PaymentResultView(viewModel, (viewType as PaymentResult).isSuccessful)
        is ThreeDS -> ThreeDSView(viewModel, (viewType as ThreeDS).url)
        else -> {}
    }
}