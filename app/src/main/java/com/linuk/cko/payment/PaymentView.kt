package com.linuk.cko.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import com.linuk.cko.R
import com.linuk.cko.ui.theme.CKOTheme
import com.linuk.cko.ui.theme.Dimen.MEDIUM


private val SLASH: String by lazy { "/" }

/**
 * Initial Screen for the payment view
 * TODO: Validate card number
 * TODO: Format card number
 * TODO: Enable Pay button when fields are validated
 * TODO: Link Pay button to WebView
 * TODO: Show credit Card scheme icon
 * TODO: Update cvv length limit base on the card type
 * TODO: Update Color Scheme
 */
@Composable
fun PaymentView() {
    var (cardNumber, setCardNumber) = remember { mutableStateOf("") }
    var (expiryDate, setExpiryDate) = remember { mutableStateOf("") }
    var (cvv, setCvv) = remember { mutableStateOf("") }
    var (cvvMaxLength, setCvvMaxLength) = remember { mutableStateOf(3) }
    var (isButtonEnabled, setIsButtonEnabled) = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(MEDIUM)
    ) {
        // CardNumber
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = cardNumber,
            singleLine = true,
            onValueChange = { setCardNumber(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            label = { Text(stringResource(R.string.card_number)) },
        )

        Row(modifier = Modifier.padding(top = MEDIUM)) {
            // Expiry Date
            TextField(
                modifier = Modifier
                    .padding(end = MEDIUM / 2)
                    .fillMaxWidth()
                    .weight(0.5f),
                value = expiryDate,
                placeholder = { Text(text = stringResource(R.string.expiry_date_placeholder)) },
                singleLine = true,
                visualTransformation = { date ->
                    val formattedDate = AnnotatedString(
                        if (date.length <= 2) {
                            date.text
                        } else {
                            val month = date.substring(0, 2)
                            val year = date.substring(2)
                            "$month$SLASH$year"
                        }
                    )
                    val offsetTranslator = object : OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int =
                            if (offset > 2) offset + 1 else offset

                        override fun transformedToOriginal(offset: Int): Int =
                            offset
                    }

                    TransformedText(formattedDate, offsetTranslator)
                },
                onValueChange =
                {
                    when (it.length) {
                        0 -> setExpiryDate(it)
                        1 -> if (it[0] == '0' || it[0] == '1') setExpiryDate(it)
                        2 -> if (
                            it[0] == '1' && it[1] - '0' in 0..2 ||
                            it[1] == '0' && it[1] - '0' in 1..9
                        ) setExpiryDate(it)
                        3, 4 -> setExpiryDate(it)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                ),
                label = { Text(stringResource(R.string.expiry_date)) },
            )

            // CVV
            TextField(
                modifier = Modifier
                    .padding(start = MEDIUM / 2)
                    .fillMaxWidth()
                    .weight(0.5f),
                value = cvv,
                singleLine = true,
                onValueChange = { if (it.length <= cvvMaxLength) setCvv(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.cvv)) },
            )
        }

        Button(
            enabled = isButtonEnabled,
            modifier = Modifier
                .padding(top = MEDIUM)
                .fillMaxWidth(),
            content = { Text(stringResource(R.string.payment_button)) },
            onClick = {
                // TODO: navigate to web view
            })
    }
}

@Composable
fun CardNumberField(
    modifier: Modifier, cardNumber: String, setCardNumber: (cardNumber: String) -> Unit
) {
    TextField(modifier = modifier,
        value = cardNumber,
        maxLines = 1,
        onValueChange = { setCardNumber(it) },
        label = { Text("Card Number") })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CKOTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            PaymentView()
        }
    }
}