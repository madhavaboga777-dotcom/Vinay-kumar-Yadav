package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val DisplayFontFamily = FontFamily(
    Font(R.font.playfair_display, FontWeight.Normal),
    Font(R.font.playfair_display, FontWeight.Bold)
)

val BodyFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

val TeluguFontFamily = FontFamily(
    Font(R.font.noto_sans_telugu, FontWeight.Normal),
    Font(R.font.noto_sans_telugu, FontWeight.Medium),
    Font(R.font.noto_sans_telugu, FontWeight.Bold)
)

fun buildAlphaTypography(isTelugu: Boolean = false): Typography {
    val activeDisplay = if (isTelugu) TeluguFontFamily else DisplayFontFamily
    val activeBody = if (isTelugu) TeluguFontFamily else BodyFontFamily
    return Typography(
        displayLarge = TextStyle(
            fontFamily = activeDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = activeDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = activeDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        titleLarge = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        titleMedium = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodySmall = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 17.sp
        ),
        labelLarge = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.2.sp
        ),
        labelMedium = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelSmall = TextStyle(
            fontFamily = activeBody,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
        )
    )
}
