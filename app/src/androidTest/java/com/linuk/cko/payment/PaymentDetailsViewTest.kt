package com.linuk.cko.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linuk.cko.payment.PaymentFixtures.buildRepository
import com.linuk.cko.payment.PaymentFixtures.buildUtils
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.DEBUG_CARD_CVV
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.DEBUG_CARD_EXP_MONTH
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.DEBUG_CARD_EXP_YEAR
import com.linuk.cko.payment.PaymentUtilsImpl.Companion.DEBUG_CARD_VALID_NUMBER
import com.linuk.cko.payment.views.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class PaymentDetailsViewTest {
    @get:Rule
    val rule = createComposeRule()

    // make post value instantly reflect
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = buildRepository()
    private val utils = buildUtils()

    @Mock
    private lateinit var stringObserver: Observer<String?>
    private lateinit var viewModel: PaymentViewModel

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        viewModel = PaymentViewModel(repository, utils)
    }

    @Test
    fun validCardNumberInputShouldUpdateVM() =
        validateTextField(viewModel.cardNumber, CARD_NUMBER_FIELD_TEST_TAG)

    @Test
    fun validExpiryMonthInputShouldUpdateVM() =
        validateTextField(viewModel.expiryMonth, EXPIRY_MONTH_FIELD_TEST_TAG)

    @Test
    fun validExpiryYearInputShouldUpdateVM() =
        validateTextField(viewModel.expiryYear, EXPIRY_YEAR_FIELD_TEST_TAG)

    @Test
    fun validCvvInputShouldUpdateVM() =
        validateTextField(viewModel.cvv, CVV_FIELD_TEST_TAG)

    @Test
    fun pressPayButtonShouldTriggerMaybePayment() {
        // given a valid credit card input
        rule.setContent { PaymentDetailsView(viewModel) }
        rule.onNodeWithTag(CARD_NUMBER_FIELD_TEST_TAG).performTextInput(DEBUG_CARD_VALID_NUMBER)
        rule.onNodeWithTag(EXPIRY_MONTH_FIELD_TEST_TAG).performTextInput(DEBUG_CARD_EXP_MONTH)
        rule.onNodeWithTag(EXPIRY_YEAR_FIELD_TEST_TAG).performTextInput(DEBUG_CARD_EXP_YEAR)
        rule.onNodeWithTag(CVV_FIELD_TEST_TAG).performTextInput(DEBUG_CARD_CVV)

        assert(!repository.isPaymentMade)
        rule.onNodeWithTag(BUTTON_TEST_TAG).performClick()
        assert(repository.isPaymentMade)
    }

    private fun validateTextField(fieldLiveData: LiveData<String>, testTag: String) {
        rule.setContent { PaymentDetailsView(viewModel) }
        fieldLiveData.observeForever(stringObserver)
        // initial number
        verify(stringObserver).onChanged("")
        rule.onNodeWithTag(testTag).apply {
            // view the field exists
            assertExists()
            // given user input text
            performTextInput("12")
            // verify VM is also updated assuming the update is valid
            verify(stringObserver).onChanged("12")
            // verify the field value is updated
            assert(hasText("12"))
        }
    }
}