package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BilingualContent
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        revealed = true
        delay(2100L)
        onFinished()
    }

    val entryAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "splashEntryAlpha"
    )
    val entryScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.92f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "splashEntryScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splashStudioGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StudioBlack, NavyMidnight, NavyRoyal, StudioBlack)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("alpha_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Red & Electric Blue ambient studio lighting canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = size.minDimension
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.42f * pulse),
                        ElectricBlue.copy(alpha = 0.12f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.24f, size.height * 0.36f),
                    radius = minDim * 0.68f
                ),
                radius = minDim * 0.68f,
                center = Offset(size.width * 0.24f, size.height * 0.36f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CinematicRed.copy(alpha = 0.40f * pulse),
                        CinematicRed.copy(alpha = 0.12f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.78f, size.height * 0.60f),
                    radius = minDim * 0.68f
                ),
                radius = minDim * 0.68f,
                center = Offset(size.width * 0.78f, size.height * 0.60f)
            )
        }

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .alpha(entryAlpha)
                .scale(entryScale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Subtle Red & Electric Blue backlit halo + crisp uncropped official logo badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = IvoryWhite,
                    shadowElevation = 18.dp,
                    border = BorderStroke(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.9f * pulse),
                                IvoryWhite,
                                CinematicRed.copy(alpha = 0.9f * pulse)
                            )
                        )
                    ),
                    modifier = Modifier.fillMaxWidth(0.88f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.alpha_media_logo),
                            contentDescription = "ALPHA MEDIA Official Brand Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = BilingualContent.TAGLINE,
                style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 3.2.sp),
                color = IvoryWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Welcome to ALPHA MEDIA",
                style = MaterialTheme.typography.bodyLarge,
                color = ElectricBlueBright,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = { sweep },
                color = CinematicRed,
                trackColor = ElectricBlue.copy(alpha = 0.28f),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = BilingualContent.SLOGAN_EN,
                style = MaterialTheme.typography.labelMedium,
                color = SlateSilver,
                textAlign = TextAlign.Center
            )
        }
    }
}
