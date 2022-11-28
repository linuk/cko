package com.linuk.cko.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.linuk.cko.payment.views.PaymentDetailsView
import com.linuk.cko.payment.views.PaymentResultView
import com.linuk.cko.payment.views.ThreeDSView

/**
 * The main view for [MainActivity], where it contains three views
 */
@Composable
fun PaymentView(viewModel: PaymentViewModel) {
    val viewType by viewModel.viewType.observeAsState()

    when (viewType) {
        ViewType.PaymentDetails -> PaymentDetailsView(viewModel)
        is ViewType.PaymentResult -> PaymentResultView(
            viewModel,
            (viewType as ViewType.PaymentResult).isSuccessful
        )
        is ViewType.ThreeDS -> ThreeDSView(viewModel, (viewType as ViewType.ThreeDS).url)
        else -> {}
    }
}