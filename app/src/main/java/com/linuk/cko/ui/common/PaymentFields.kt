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
    modifier: Modifier = Modifier,
    cardNumber: String,
    onValueChange: (number: String) -> Unit,
    isError: Boolean,
    enabled: Boolean,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = cardNumber,
        singleLine = true,
        enabled = enabled,
        isError = isError,
        onValueChange = onValueChange,
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(if (!isError) R.string.card_number else R.string.card_number_incorrect)) },
    )
}

@Composable
fun ExpiryMonthField(
    modifier: Modifier = Modifier,
    expiryMonth: String,
    onValueChange: (month: String) -> Unit,
    enabled: Boolean,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = expiryMonth,
        enabled = enabled,
        placeholder = { Text(text = stringResource(R.string.expiry_month_placeholder)) },
        singleLine = true,
        onValueChange = onValueChange,
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(R.string.expiry_month)) },
    )
}

@Composable
fun ExpiryYearField(
    modifier: Modifier = Modifier,
    expiryYear: String,
    onValueChange: (year: String) -> Unit,
    enabled: Boolean,
) {
    // Expiry Year
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = expiryYear,
        enabled = enabled,
        placeholder = { Text(text = stringResource(R.string.expiry_year_placeholder)) },
        singleLine = true,
        onValueChange = onValueChange,
        keyboardOptions = KEYBOARD_OPTIONS_NEXT,
        label = { Text(stringResource(R.string.expiry_year)) },
    )
}

@Composable
fun CvvField(
    modifier: Modifier = Modifier,
    cvv: String,
    onValueChange: (cvv: String) -> Unit,
    enabled: Boolean,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = cvv,
        singleLine = true,
        enabled = enabled,
        onValueChange = onValueChange,
        keyboardOptions = KEYBOARD_OPTIONS_DONE,
        label = { Text(stringResource(R.string.cvv)) },
    )
}
