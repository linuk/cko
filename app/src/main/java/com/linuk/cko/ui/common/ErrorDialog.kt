package com.linuk.cko.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.linuk.cko.ui.theme.Dimen
import com.linuk.cko.ui.theme.Red400

@Composable
fun ErrorDialog(
    errorMessage: String,
    setErrorMessage: (message: String) -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(Dimen.MEDIUM),
        onDismissRequest = { setErrorMessage("") },
        text = { Text(errorMessage) },
        buttons = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.MEDIUM),
                onClick = { setErrorMessage("") },
                content = { Text("DONE") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Red400,
                    contentColor = Color.White
                )
            )
        })
}