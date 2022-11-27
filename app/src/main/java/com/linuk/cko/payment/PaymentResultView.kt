package com.linuk.cko.payment

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linuk.cko.R
import com.linuk.cko.data.PaymentRepository
import com.linuk.cko.ui.theme.CKOTheme
import com.linuk.cko.ui.theme.Cyan400
import com.linuk.cko.ui.theme.Dimen
import com.linuk.cko.ui.theme.Red400

@Composable
fun PaymentResultView(viewModel: PaymentViewModel, isSuccessful: Boolean) {
    val activity = LocalContext.current as? Activity

    BackHandler {
        viewModel.onViewChanged(ViewType.PaymentDetails)
    }

    val icon = if (isSuccessful) Icons.Rounded.Check else Icons.Rounded.Warning
    val color = if (isSuccessful) Cyan400 else Red400
    val title = stringResource(
        if (isSuccessful) R.string.payment_succeed_title
        else R.string.payment_fail_title
    )
    val description = stringResource(
        if (isSuccessful) R.string.payment_succeed_description
        else R.string.payment_fail_description
    )
    val buttonText = stringResource(
        if (isSuccessful) R.string.payment_succeed_button
        else R.string.payment_fail_button
    )

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.LARGE)
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            tint = color,
            contentDescription = null,
            modifier = Modifier
                .width(128.dp)
                .height(128.dp)
        )
        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Dimen.LARGE),
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = Dimen.SMALL),
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(top = Dimen.LARGE * 2),
            colors = buttonColors(backgroundColor = color),
            content = { Text(text = buttonText, color = Color.White) },
            onClick = {
                if (isSuccessful) activity?.finish()
                else viewModel.onViewChanged(ViewType.PaymentDetails)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentResultSuccessPreview() {
    CKOTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            PaymentResultView(PaymentViewModel(PaymentRepository(), PaymentUtils()), true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentResultFailPreview() {
    CKOTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            PaymentResultView(PaymentViewModel(PaymentRepository(), PaymentUtils()), false)
        }
    }
}