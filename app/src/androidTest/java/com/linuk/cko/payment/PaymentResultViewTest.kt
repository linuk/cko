package com.linuk.cko.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linuk.cko.data.PaymentRepositoryImpl
import com.linuk.cko.payment.views.PaymentResultView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TODO: verify Done button action on successful view
 */
@RunWith(AndroidJUnit4::class)
class PaymentResultViewTest {
    private val viewModel = PaymentViewModel(PaymentRepositoryImpl(), PaymentUtilsImpl())

    @get:Rule
    val rule = createComposeRule()

    // make post value instantly reflect
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun verifySuccessfulViewElements() {
        rule.setContent { PaymentResultView(viewModel = viewModel, isSuccessful = true) }
        rule.onNodeWithText("Payment Completed!").assertExists()
        rule.onNodeWithText("The transaction has been completed successfully!").assertExists()

        rule.onNodeWithText("Done").assertExists()
        rule.onNodeWithText("Done").performClick()
        assert(viewModel.viewType.value is ViewType.PaymentDetails)
    }

    @Test
    fun verifyFailureViewElements() {
        rule.setContent { PaymentResultView(viewModel = viewModel, isSuccessful = false) }
        rule.onNodeWithText("Payment Failed").assertExists()
        rule.onNodeWithText("Don't worry you will not be charged.").assertExists()
    }

    @Test
    fun verifyFailureViewButtonClickShouldRedirectToPaymentDetailsView() {
        // set viewType to
        rule.setContent { PaymentResultView(viewModel = viewModel, isSuccessful = false) }
        viewModel.onViewChanged(ViewType.PaymentResult(false))
        assert(viewModel.viewType.value is ViewType.PaymentResult)

        rule.onNodeWithText("Retry").assertExists()
        rule.onNodeWithText("Retry").performClick()
        assert(viewModel.viewType.value is ViewType.PaymentDetails)
    }
}