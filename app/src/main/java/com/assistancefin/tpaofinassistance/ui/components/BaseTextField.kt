package com.assistancefin.tpaofinassistance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.ui.theme.GrayIcon
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import com.assistancefin.tpaofinassistance.utils.filterIntegerInput

@Composable
fun BaseTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxChar: Int = 20,
    textColor: Color = Color.Black,
    singleLine: Boolean = true,
    showSuffix: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { new ->
            if (label == context.getString(R.string.sum_name)) {
                if (new.length <= maxChar) onValueChange(filterIntegerInput(new))
            } else {
                if (new.length <= maxChar) onValueChange(new)
            }
        },
        label = { Text(label, color = GrayIcon) },
        singleLine = singleLine,
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            }
        },
        suffix = {
            if (showSuffix) {
                Text(text = "₺", color = RedBg, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
        shape = RoundedCornerShape(5.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = GrayIcon,
            unfocusedBorderColor = GrayIcon,
            cursorColor = GrayIcon
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )
}


@Preview
@Composable
private fun Preview() {
    BaseTextField(
        label = "kjdsvjkhdsjkh",
        value = "",
        showSuffix = false,
        onValueChange = {}
    )
}

