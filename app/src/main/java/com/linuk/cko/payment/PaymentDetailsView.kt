package com.linuk.cko.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
 */
@OptIn(ExperimentalComposeUiApi::class) // For Keyboard controller
@Composable
fun PaymentDetailsView(viewModel: PaymentViewModel) {
    val cardNumber by viewModel.cardNumber.observeAsState("")
    val expiryMonth by viewModel.expiryMonth.observeAsState("")
    val expiryYear by viewModel.expiryYear.observeAsState("")
    val cvv by viewModel.cvv.observeAsState("")
    val cardType by viewModel.cardType.observeAsState(CardType.DEFAULT)
    val buttonEnabled by viewModel.buttonEnabled.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)
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
        errorMessage?.let { ErrorDialog(errorMessage!!) { viewModel.onErrorDialogDone() } }

        Column(
            modifier = Modifier.padding(MEDIUM), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CardNumberField(
                    modifier = Modifier.weight(0.8f), cardNumber,
                    onValueChange = { number ->
                        viewModel.onCardNumberChanged(number)
                    },
                    enabled = !isLoading
                )

                getCardTypeImageRes(cardType)?.let { imageRes ->
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = cardType.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.2f),
                    )
                }
            }

            Row(modifier = Modifier.padding(top = FIELD_SPACE / 2)) {
                // Expiry Month
                ExpiryMonthField(
                    modifier = Modifier
                        .padding(end = FIELD_SPACE)
                        .weight(0.4f),
                    expiryMonth = expiryMonth,
                    onValueChange = { viewModel.onExpiryMonthChanged(it) },
                    enabled = !isLoading,
                )

                // Expiry Year
                ExpiryYearField(
                    modifier = Modifier
                        .padding(end = FIELD_SPACE)
                        .weight(0.4f),
                    expiryYear = expiryYear,
                    onValueChange = { viewModel.onExpiryYearChanged(it) },
                    enabled = !isLoading,
                )

                // CVV
                CvvField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
                    cvv = cvv,
                    onValueChange = { viewModel.onCvvChanged(it) },
                    enabled = !isLoading,
                )
            }

            Button(enabled = buttonEnabled && !isLoading,
                modifier = Modifier
                    .padding(top = FIELD_SPACE)
                    .fillMaxWidth(),
                content = { Text(stringResource(if (isLoading) R.string.payment_button_loading else R.string.payment_button)) },
                onClick = { handlePaymentButtonClick(viewModel, keyboardController) })

            // DEBUG Usage
            Row {
                OutlinedButton(modifier = Modifier
                    .padding(top = FIELD_SPACE, end = FIELD_SPACE / 2)
                    .fillMaxWidth()
                    .weight(0.5f),
                    content = { Text("Fill Valid Card") },
                    onClick = { onDebugButtonClick(DEBUG_CARD_VALID_NUMBER, viewModel) })

                OutlinedButton(modifier = Modifier
                    .padding(top = FIELD_SPACE, start = FIELD_SPACE / 2)
                    .fillMaxWidth()
                    .weight(0.5f),
                    content = { Text("Fill Invalid Card") },
                    onClick = { onDebugButtonClick(DEBUG_CARD_INVALID_NUMBER, viewModel) })
            }
        }
    }
}

private fun onDebugButtonClick(cardNumber: String, viewModel: PaymentViewModel) {
    viewModel.onCardNumberChanged(cardNumber)
    viewModel.onExpiryMonthChanged(DEBUG_CARD_EXP_MONTH)
    viewModel.onExpiryYearChanged(DEBUG_CARD_EXP_YEAR)
    viewModel.onCvvChanged(DEBUG_CARD_CVV)
}

@OptIn(ExperimentalComposeUiApi::class)
private fun handlePaymentButtonClick(
    viewModel: PaymentViewModel,
    keyboardController: SoftwareKeyboardController?,
) {
    viewModel.onButtonPressed()
    keyboardController?.hide()
    if (viewModel.areFieldsValid()) {
        val cardDetails = CardDetails(
            number = viewModel.cardNumber.value ?: "",
            expiryMonth = viewModel.expiryMonth.value ?: "",
            expiryYear = viewModel.expiryYear.value ?: "",
            cvv = viewModel.cvv.value ?: ""
        )
        PaymentRepository().makePayment(
            cardDetails, { url ->
                viewModel.onPaymentRequestDone()
                viewModel.onViewChanged(ViewType.ThreeDS(url))
            }, { message ->
                viewModel.onPaymentRequestDone()
                viewModel.onErrorMessageReceived(message)
            })
    } else {
        viewModel.onPaymentRequestDone()
    }
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