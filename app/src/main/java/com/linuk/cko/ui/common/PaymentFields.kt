package com.linuk.cko.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.linuk.cko.R

private val KEYBOARD_OPTIONS_NEXT by lazy {
    KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next
    )
}

internal val KEYBOARD_OPTIONS_DONE by lazy { KEYBOARD_OPTIONS_NEXT.copy(imeAction = ImeAction.Done) }

@Composable
fun CardNumberField(
    cardNumber: String,
    setCardNumber: (number: String) -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = cardNumber,
        singleLine = true,
        enabled = enabled,
        onValueChange = { setCardNumber(it) },
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(R.string.card_number)) },
    )
}

@Composable
fun ExpiryMonthField(
    modifier: Modifier = Modifier,
    expiryMonth: String,
    setExpiryMonth: (month: String) -> Unit,
    enabled: Boolean,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = expiryMonth,
        enabled = enabled,
        placeholder = { Text(text = stringResource(R.string.expiry_month_placeholder)) },
        singleLine = true,
        onValueChange = { month -> if (isMonthValid(month)) setExpiryMonth(month) },
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(R.string.expiry_month)) },
    )
}

private fun isMonthValid(month: String) = month.isEmpty() || month.toInt() <= 12

@Composable
fun ExpiryYearField(
    modifier: Modifier = Modifier,
    expiryYear: String,
    setExpiryYear: (month: String) -> Unit,
    enabled: Boolean,
) {
    // Expiry Year
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = expiryYear,
        enabled = enabled,
        placeholder = { Text(text = stringResource(R.string.expiry_year_placeholder)) },
        singleLine = true,
        onValueChange = { year -> if (isYearValid(year)) setExpiryYear(year) },
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(R.string.expiry_year)) },
    )
}

private fun isYearValid(year: String) = year.length <= 4

@Composable
fun CvvField(
    modifier: Modifier = Modifier,
    cvv: String,
    setCvv: (month: String) -> Unit,
    cvvMaxLength: Int,
    enabled: Boolean,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = cvv,
        singleLine = true,
        enabled = enabled,
        onValueChange = { if (it.length <= cvvMaxLength) setCvv(it) },
        keyboardOptions = KEYBOARD_OPTIONS_DONE,
        label = { Text(stringResource(R.string.cvv)) },
    )
}
