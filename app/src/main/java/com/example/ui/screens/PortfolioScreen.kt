package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.model.PortfolioItem
import com.example.model.UiState
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaCard
import com.example.ui.components.AlphaLogoBannerHeader
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicRedBright
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import kotlinx.coroutines.delay

@Composable
fun PortfolioScreen(
    language: AppLanguage,
    portfolioState: UiState<List<PortfolioItem>>,
    onBookSimilar: (PortfolioItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var previewItem by remember { mutableStateOf<PortfolioItem?>(null) }

    val items = (portfolioState as? UiState.Success)?.data ?: BilingualContent.defaultPortfolioItems
    val filtered = remember(items, selectedCategory) {
        if (selectedCategory == "All") items else items.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StudioBlack, NavyRoyal, StudioBlack))
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AlphaLogoBannerHeader(
                eyebrow = BilingualContent.t(language, "CONCEPT SHOWCASE", "కాన్సెప్ట్ గ్యాలరీ"),
                title = BilingualContent.t(language, "Portfolio Gallery", "పోర్ట్‌ఫోలియో గ్యాలరీ"),
                subtitle = BilingualContent.t(
                    language,
                    "Explore AI advertisement concepts across local & commercial categories.",
                    "స్థానిక వ్యాపార వర్గాల వారీగా AI ప్రకటనల కాన్సెప్ట్‌లను చూడండి."
                )
            )
            Spacer(Modifier.height(12.dp))

            // Strict non-fabrication notice
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = ElectricBlueBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = BilingualContent.t(
                            language,
                            "Demonstration projects below are AI Concept Advertisements. ALPHA MEDIA never invents client testimonials or business results.",
                            "క్రింది ప్రాజెక్టులు AI కాన్సెప్ట్ ప్రకటనల ప్రదర్శనలు మాత్రమే. మేము కల్పిత క్లయింట్ ఫలితాలను చూపము."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateSilver
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(BilingualContent.PORTFOLIO_CATEGORIES) { cat ->
                    val label = if (language == AppLanguage.TELUGU) {
                        BilingualContent.PORTFOLIO_CATEGORIES_TE[cat] ?: cat
                    } else {
                        cat
                    }
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = IvoryWhite,
                            containerColor = NavySurface,
                            labelColor = IvoryWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == cat,
                            borderColor = NavyBorder
                        )
                    )
                }
            }
        }

        items(filtered, key = { it.id.ifBlank { it.titleEn } }) { item ->
            AlphaCard(
                modifier = Modifier.clickable { previewItem = item }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    ) {
                        PortfolioVisualArtwork(visualKey = item.visualKey)

                        // AI Concept Advertisement badge
                        Surface(
                            color = StudioBlack.copy(alpha = 0.88f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ElectricBlue),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = BilingualContent.t(
                                    language,
                                    "AI Concept Advertisement",
                                    "AI కాన్సెప్ట్ ప్రకటన"
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricBlueBright,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Cinematic Red play preview pill
                        Surface(
                            color = CinematicRed,
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, IvoryWhite),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Preview Concept",
                                    tint = IvoryWhite,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Surface(
                            color = StudioBlack.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = item.durationLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = IvoryWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.TELUGU) {
                                BilingualContent.PORTFOLIO_CATEGORIES_TE[item.category] ?: item.category
                            } else {
                                item.category
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricBlueBright
                        )
                        Text(
                            text = BilingualContent.t(language, item.titleEn, item.titleTe),
                            style = MaterialTheme.typography.titleLarge,
                            color = IvoryWhite
                        )
                        Text(
                            text = BilingualContent.t(language, item.descriptionEn, item.descriptionTe),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateSilver
                        )
                        Text(
                            text = BilingualContent.t(language, item.conceptNoticeEn, item.conceptNoticeTe),
                            style = MaterialTheme.typography.labelSmall,
                            color = CinematicRedBright
                        )
                    }
                }
            }
        }

        item {
            AlphaBrandFooter(language = language)
        }
    }

    previewItem?.let { item ->
        PortfolioPreviewDialog(
            language = language,
            item = item,
            onDismiss = { previewItem = null },
            onBookSimilar = {
                previewItem = null
                onBookSimilar(item)
            }
        )
    }
}

@Composable
private fun PortfolioVisualArtwork(visualKey: String) {
    when (visualKey.lowercase()) {
        "dairy" -> Image(
            painter = painterResource(id = R.drawable.img_portfolio_dairy),
            contentDescription = "Dairy AI Concept Advertisement",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        "restaurant" -> Image(
            painter = painterResource(id = R.drawable.img_portfolio_restaurant),
            contentDescription = "Restaurant AI Concept Advertisement",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        "clothing" -> Image(
            painter = painterResource(id = R.drawable.img_portfolio_clothing),
            contentDescription = "Clothing Store AI Concept Advertisement",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(StudioBlack, NavyElevated, NavySurface)
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = ElectricBlue.copy(alpha = 0.28f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width * 0.78f, size.height * 0.35f)
                    )
                    drawCircle(
                        color = CinematicRed.copy(alpha = 0.24f),
                        radius = size.minDimension * 0.28f,
                        center = Offset(size.width * 0.22f, size.height * 0.72f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioPreviewDialog(
    language: AppLanguage,
    item: PortfolioItem,
    onDismiss: () -> Unit,
    onBookSimilar: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, item.id) {
        while (isPlaying) {
            delay(100L)
            progress = (progress + 0.02f) % 1.0f
        }
    }

    val steps = item.storyboardSteps.ifEmpty { listOf("Opening Concept Frame", "Brand Lockup") }
    val currentBeatIndex = ((progress * steps.size).toInt()).coerceIn(0, steps.lastIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, ElectricBlue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = BilingualContent.t(language, "AI Concept Preview", "AI కాన్సెప్ట్ ప్రివ్యూ"),
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricBlueBright
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = IvoryWhite)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    PortfolioVisualArtwork(visualKey = item.visualKey)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(StudioBlack.copy(alpha = 0.45f))
                    )
                    Surface(
                        color = StudioBlack.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = steps[currentBeatIndex],
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricBlueBright,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { isPlaying = !isPlaying }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Toggle Preview Playback",
                            tint = CinematicRedBright
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        color = CinematicRed,
                        trackColor = NavyBorder,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(item.durationLabel, style = MaterialTheme.typography.labelSmall, color = SlateSilver)
                }

                Text(
                    text = BilingualContent.t(language, item.titleEn, item.titleTe),
                    style = MaterialTheme.typography.titleLarge,
                    color = IvoryWhite
                )
                Text(
                    text = BilingualContent.t(language, item.conceptNoticeEn, item.conceptNoticeTe),
                    style = MaterialTheme.typography.labelSmall,
                    color = CinematicRedBright
                )

                steps.forEachIndexed { idx, step ->
                    Text(
                        text = "${idx + 1}. $step",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (idx == currentBeatIndex) ElectricBlueBright else SlateSilver
                    )
                }

                AlphaPrimaryButton(
                    text = BilingualContent.t(language, "Book Similar AI Ad", "ఇలాంటి AI ప్రకటన బుక్ చేయండి"),
                    onClick = onBookSimilar,
                    icon = Icons.Filled.AutoAwesome,
                    useRedAccent = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
