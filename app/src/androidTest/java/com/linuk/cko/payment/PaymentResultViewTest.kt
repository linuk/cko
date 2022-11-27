package com.linuk.cko.payment

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linuk.cko.data.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TODO: cover Done button action on successful view
 */
@RunWith(AndroidJUnit4::class)
class PaymentResultViewTest {
    private val viewModel = PaymentViewModel(PaymentRepository(), PaymentUtils())

    @get:Rule
    val rule = createComposeRule()

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
        runBlocking(Dispatchers.Main) {
            viewModel.onViewChangedOnMainThread(ViewType.PaymentResult(false))
        }
        assert(viewModel.viewType.value is ViewType.PaymentResult)

        rule.onNodeWithText("Retry").assertExists()
        rule.onNodeWithText("Retry").performClick()
        assert(viewModel.viewType.value is ViewType.PaymentDetails)
    }
}