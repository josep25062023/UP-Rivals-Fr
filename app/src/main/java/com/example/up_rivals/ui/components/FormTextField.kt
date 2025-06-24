// En: ui/components/FormTextField.kt
package com.example.up_rivals.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme


@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null // <-- PARÁMETRO NUEVO
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelText) },
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon, // <-- LO USAMOS AQUÍ
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = LightBlueBackground,
            focusedContainerColor = LightBlueBackground,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = SubtleGrey
        )
    )
}


// --- Preview para ver nuestros TextFields con el nuevo estilo ---
@Preview(showBackground = true)
@Composable
fun FormTextFieldPreview() {
    UPRivalsTheme {
        Column {
            FormTextField(
                value = "",
                onValueChange = {},
                labelText = "Nombre de usuario"
            )
            FormTextField(
                value = "12345",
                onValueChange = {},
                labelText = "Contraseña",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}