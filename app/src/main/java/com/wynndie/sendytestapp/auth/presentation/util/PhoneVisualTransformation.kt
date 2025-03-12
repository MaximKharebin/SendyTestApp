package com.wynndie.sendytestapp.auth.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = buildString {
            text.forEachIndexed { index, _ ->
                append(text[index])
                when (index) {
                    2, 5, 7 -> append(" ")
                    else -> append("")
                }
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return when {
                        offset <= 2 -> offset
                        offset in 3..5 -> offset + 1
                        offset in 6..7 -> offset + 2
                        offset in 8..9 -> offset + 3
                        else -> offset + 3
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return when {
                        offset <= 2 -> offset
                        offset in 3..6 -> offset - 1
                        offset in 7..9 -> offset - 2
                        offset in 10..13 -> offset - 3
                        else -> offset
                    }
                }
            }
        )
    }
}