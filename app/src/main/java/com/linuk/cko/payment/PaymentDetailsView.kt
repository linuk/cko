package com.linuk.cko.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linuk.cko.R
import com.linuk.cko.ui.common.*
import com.linuk.cko.ui.theme.CKOTheme
import com.linuk.cko.ui.theme.Dimen.MEDIUM
import com.linuk.cko.ui.theme.Dimen.SMALL


private val FIELD_SPACE by lazy { SMALL }

/**
 * Initial Screen for the payment view
 * TODO: Validate card number
 * TODO: Format card number
 * TODO: Enable Pay button when fields are validated
 * TODO: Show credit Card scheme icon
 * TODO: Update cvv length limit base on the card type
 */
@OptIn(ExperimentalComposeUiApi::class) // For Keyboard controller
@Composable
fun PaymentDetailsView(
    viewModel: PaymentViewModel
) {
    var (cardNumber, setCardNumber) = remember { mutableStateOf("") }
    var (expiryMonth, setExpiryMonth) = remember { mutableStateOf("") }
    var (expiryYear, setExpiryYear) = remember { mutableStateOf("") }
    var (cvv, setCvv) = remember { mutableStateOf("") }
    var (cvvMaxLength, setCvvMaxLength) = remember { mutableStateOf(3) }
    var (isButtonEnabled, setIsButtonEnabled) = remember { mutableStateOf(true) }
    var (isLoading, setIsLoading) = remember { mutableStateOf(false) }
    var (errorMessage, setErrorMessage) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(FIELD_SPACE)
    ) {
        if (errorMessage.isNotEmpty()) ErrorDialog(errorMessage, setErrorMessage)

        Column(
            modifier = Modifier.padding(MEDIUM), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardNumberField(cardNumber, setCardNumber, !isLoading)

            Row(modifier = Modifier.padding(top = FIELD_SPACE / 2)) {
                // Expiry Month
                ExpiryMonthField(
                    Modifier
                        .padding(end = FIELD_SPACE)
                        .weight(0.4f),
                    expiryMonth, setExpiryMonth, !isLoading,
                )

                // Expiry Year
                ExpiryYearField(
                    modifier = Modifier
                        .padding(end = FIELD_SPACE)
                        .weight(0.4f),
                    expiryYear, setExpiryYear, !isLoading,
                )

                // CVV
                CvvField(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
                    cvv, setCvv, cvvMaxLength, !isLoading,
                )
            }

            Button(enabled = isButtonEnabled && !isLoading,
                modifier = Modifier
                    .padding(top = FIELD_SPACE)
                    .fillMaxWidth(),
                content = { Text(stringResource(if (isLoading) R.string.payment_button_loading else R.string.payment_button)) },
                onClick = {
                    handlePaymentButtonClick(
                        cardNumber = cardNumber,
                        expiryMonth = expiryMonth,
                        expiryYear = expiryYear,
                        cvv = cvv,
                        viewModel = viewModel,
                        setIsLoading = setIsLoading,
                        setErrorMessage = setErrorMessage,
                        keyboardController = keyboardController
                    )
                })

            // DEBUG Usage
            Row {
                OutlinedButton(modifier = Modifier
                    .padding(top = FIELD_SPACE, end = FIELD_SPACE / 2)
                    .fillMaxWidth()
                    .weight(0.5f),
                    content = { Text("Fill Valid Card") },
                    onClick = {
                        setCvv("100")
                        setExpiryMonth("06")
                        setExpiryYear("2030")
                        setCardNumber("4242424242424242")
                    })

                OutlinedButton(modifier = Modifier
                    .padding(
                        top = FIELD_SPACE, start = FIELD_SPACE / 2
                    )
                    .fillMaxWidth()
                    .weight(0.5f),
                    content = { Text("Fill Invalid Card") },
                    onClick = {
                        setCvv("100")
                        setExpiryMonth("06")
                        setExpiryYear("2030")
                        setCardNumber("4243754271700719")
                    })
            }
        }
    }
}

private fun isYearValid(year: String) = year.length <= 4

@OptIn(ExperimentalComposeUiApi::class)
private fun handlePaymentButtonClick(
    cardNumber: String,
    expiryMonth: String,
    expiryYear: String,
    cvv: String,
    viewModel: PaymentViewModel,
    setIsLoading: (isLoading: Boolean) -> Unit,
    setErrorMessage: (message: String) -> Unit,
    keyboardController: SoftwareKeyboardController?,
) {
    setIsLoading(true)
    keyboardController?.hide()
    PaymentRepository().makePayment(
        // TODO: Use real input from the form
        CardDetails(
            number = cardNumber, expiryMonth = expiryMonth, expiryYear = expiryYear, cvv = cvv
        ), { url ->
            setIsLoading(false)
            viewModel.onViewChanged(ViewType.ThreeDS(url))
        }, { message ->
            setErrorMessage(message)
            setIsLoading(false)
        })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CKOTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            PaymentDetailsView(PaymentViewModel())
        }
    }
}