package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BilingualContent
import com.example.model.AgencyService
import com.example.model.AiResearchState
import com.example.model.AiStrategyState
import com.example.model.AppLanguage
import com.example.model.UiState
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaCard
import com.example.ui.components.AlphaLogoBannerHeader
import com.example.ui.components.AlphaOutlineButton
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.SkyInfo
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack

@Composable
fun ServicesAndAiScreen(
    language: AppLanguage,
    servicesState: UiState<List<AgencyService>>,
    aiResearchState: AiResearchState,
    aiStrategyState: AiStrategyState,
    onBookService: (AgencyService) -> Unit,
    onRunMarketResearch: (businessType: String, region: String, goal: String) -> Unit,
    onRunDeepStrategy: (businessName: String, product: String, adType: String, adLang: String, duration: String, notes: String) -> Unit,
    onUseStrategyInBooking: (businessName: String, product: String, adType: String, adLang: String, duration: String, brief: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StudioBlack, NavyRoyal, StudioBlack))
            )
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = StudioBlack,
            contentColor = ElectricBlueBright,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CinematicRed
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.testTag("tab_10_services"),
                text = {
                    Text(
                        BilingualContent.t(language, "10 AI Services", "10 AI సేవలు"),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.testTag("tab_ai_creative_lab"),
                text = {
                    Text(
                        BilingualContent.t(language, "AI Strategy & Trends", "AI స్ట్రాటజీ & ట్రెండ్స్"),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (selectedTab == 0) {
            val services = (servicesState as? UiState.Success)?.data ?: BilingualContent.defaultServices
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    AlphaLogoBannerHeader(
                        eyebrow = BilingualContent.t(language, "CREATIVE PRODUCTION", "క్రియేటివ్ ప్రొడక్షన్"),
                        title = BilingualContent.t(language, "ALPHA MEDIA AI Services", "ALPHA MEDIA AI సేవలు"),
                        subtitle = BilingualContent.t(
                            language,
                            "10 tailored AI advertising & production packages for local and growing brands.",
                            "స్థానిక మరియు ఎదుగుతున్న బ్రాండ్ల కోసం 10 ప్రత్యేక AI ప్రకటన సేవలు."
                        )
                    )
                }
                items(services, key = { it.id.ifBlank { "${it.orderIndex}_${it.titleEn}" } }) { service ->
                    AlphaCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = if (service.orderIndex % 2L == 1L) ElectricBlue.copy(alpha = 0.2f) else CinematicRed.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (service.orderIndex % 2L == 1L) ElectricBlue else CinematicRed
                                    )
                                ) {
                                    Text(
                                        text = "#${service.orderIndex.toString().padStart(2, '0')}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = IvoryWhite,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = BilingualContent.t(language, service.deliverablesEn, service.deliverablesTe),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricBlueBright
                                )
                            }

                            Text(
                                text = BilingualContent.t(language, service.titleEn, service.titleTe),
                                style = MaterialTheme.typography.titleLarge,
                                color = IvoryWhite
                            )
                            Text(
                                text = BilingualContent.t(language, service.descriptionEn, service.descriptionTe),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateSilver
                            )
                            AlphaOutlineButton(
                                text = BilingualContent.t(language, "BOOK YOUR AI AD", "ఈ సేవ బుక్ చేయండి"),
                                onClick = { onBookService(service) },
                                icon = Icons.Filled.MovieCreation,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    AlphaBrandFooter(language = language)
                }
            }
        } else {
            AiCreativeLabContent(
                language = language,
                aiResearchState = aiResearchState,
                aiStrategyState = aiStrategyState,
                onRunMarketResearch = onRunMarketResearch,
                onRunDeepStrategy = onRunDeepStrategy,
                onUseStrategyInBooking = onUseStrategyInBooking
            )
        }
    }
}

@Composable
private fun AiCreativeLabContent(
    language: AppLanguage,
    aiResearchState: AiResearchState,
    aiStrategyState: AiStrategyState,
    onRunMarketResearch: (String, String, String) -> Unit,
    onRunDeepStrategy: (String, String, String, String, String, String) -> Unit,
    onUseStrategyInBooking: (String, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricBlue,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = IvoryWhite,
        unfocusedTextColor = IvoryWhite,
        focusedLabelColor = ElectricBlueBright,
        unfocusedLabelColor = SlateSilver
    )

    var researchBusiness by remember { mutableStateOf("Dairy & Fresh Milk") }
    var researchRegion by remember { mutableStateOf("Pithapuram & East Godavari, Telugu") }
    var researchGoal by remember { mutableStateOf("Festive social media reel campaign") }

    var stratBusiness by remember { mutableStateOf("") }
    var stratProduct by remember { mutableStateOf("") }
    var stratAdType by remember { mutableStateOf(BilingualContent.AD_TYPES.first()) }
    var stratLang by remember { mutableStateOf("Telugu") }
    var stratDuration by remember { mutableStateOf("30 seconds") }
    var stratNotes by remember { mutableStateOf("") }
    var showThoughts by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AlphaLogoBannerHeader(
                eyebrow = BilingualContent.t(language, "ALPHA MEDIA AI STUDIO", "ALPHA MEDIA AI స్టూడియో"),
                title = BilingualContent.t(language, "AI Strategy & Live Trends", "AI స్ట్రాటజీ & లైవ్ ట్రెండ్స్"),
                subtitle = BilingualContent.t(
                    language,
                    "Google Search market research & High-Thinking bilingual script director.",
                    "Google Search మార్కెట్ పరిశోధన & High-Thinking తెలుగు/ఇంగ్లీష్ స్క్రిప్ట్ డైరెక్టర్."
                )
            )
        }

        // 1. GOOGLE SEARCH GROUNDING CARD
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "GOOGLE SEARCH GROUNDING", "గూగుల్ సెర్చ్ ఆధారిత పరిశోధన"),
                title = BilingualContent.t(language, "Live Market & Ad Trend Finder", "లైవ్ మార్కెట్ & ట్రెండ్స్ అన్వేషణ"),
                subtitle = BilingualContent.t(
                    language,
                    "Grounded with real-time Google Search data via Gemini for current regional & seasonal ad ideas.",
                    "తాజా ప్రాంతీయ మరియు పండుగ ప్రకటనల ట్రెండ్స్ కోసం Google Search సమాచారంతో పనిచేస్తుంది."
                )
            )
            Spacer(Modifier.height(10.dp))
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = researchBusiness,
                        onValueChange = { researchBusiness = it },
                        label = { Text(BilingualContent.t(language, "Business Category", "వ్యాపార వర్గం")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = researchRegion,
                        onValueChange = { researchRegion = it },
                        label = { Text(BilingualContent.t(language, "Region & Language", "ప్రాంతం & భాష")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = researchGoal,
                        onValueChange = { researchGoal = it },
                        label = { Text(BilingualContent.t(language, "Campaign Goal", "ప్రచార లక్ష్యం")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AlphaPrimaryButton(
                        text = BilingualContent.t(language, "Search Live Ad Trends", "లైవ్ ట్రెండ్స్ వెతకండి"),
                        onClick = { onRunMarketResearch(researchBusiness, researchRegion, researchGoal) },
                        icon = Icons.Filled.Search,
                        enabled = aiResearchState !is AiResearchState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_search_grounding_button")
                    )

                    when (aiResearchState) {
                        AiResearchState.Idle -> Unit
                        AiResearchState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ElectricBlueBright,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Searching current trends with Google Search...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricBlueBright
                                )
                            }
                        }
                        is AiResearchState.Error -> {
                            Surface(
                                color = CrimsonContainer,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CrimsonError)
                            ) {
                                Text(
                                    aiResearchState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IvoryWhite,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        is AiResearchState.Success -> {
                            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                            Text(
                                text = aiResearchState.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = IvoryWhite
                            )
                            if (aiResearchState.sources.isNotEmpty()) {
                                Text(
                                    text = BilingualContent.t(language, "Google Search Sources:", "గూగుల్ సెర్చ్ ఆధారాలు:"),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ElectricBlueBright,
                                    fontWeight = FontWeight.Bold
                                )
                                aiResearchState.sources.take(5).forEach { source ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                runCatching {
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.url)))
                                                }
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Link,
                                            contentDescription = null,
                                            tint = SkyInfo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = source.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SkyInfo
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. HIGH-THINKING DEEP CREATIVE STRATEGIST CARD
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "HIGH THINKING CREATIVE DIRECTOR", "లోతైన ఆలోచన AI డైరెక్టర్"),
                title = BilingualContent.t(language, "AI Script & Storyboard Architect", "AI స్క్రిప్ట్ & స్టోరీబోర్డ్ రూపకల్పన"),
                subtitle = BilingualContent.t(
                    language,
                    "Deep multi-step reasoning produces a scene-by-scene visual plan and bilingual Telugu + English script.",
                    "లోతైన విశ్లేషణతో దృశ్యాల వారీ స్టోరీబోర్డ్ మరియు తెలుగు + ఇంగ్లీష్ స్క్రిప్ట్ సిద్ధం చేస్తుంది."
                )
            )
            Spacer(Modifier.height(10.dp))
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = stratBusiness,
                        onValueChange = { stratBusiness = it },
                        label = { Text(BilingualContent.t(language, "Business Name *", "వ్యాపారం పేరు *")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stratProduct,
                        onValueChange = { stratProduct = it },
                        label = { Text(BilingualContent.t(language, "Product / Service *", "ఉత్పత్తి / సేవ *")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = BilingualContent.t(language, "Language", "భాష"),
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricBlueBright
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Telugu", "English", "Hindi").forEach { langOpt ->
                            FilterChip(
                                selected = stratLang == langOpt,
                                onClick = { stratLang = langOpt },
                                label = { Text(langOpt) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue,
                                    selectedLabelColor = IvoryWhite,
                                    containerColor = NavyElevated,
                                    labelColor = IvoryWhite
                                )
                            )
                        }
                    }
                    OutlinedTextField(
                        value = stratNotes,
                        onValueChange = { stratNotes = it },
                        label = { Text(BilingualContent.t(language, "Target Audience / Offer Details", "లక్ష్య ప్రేక్షకులు / ఆఫర్ వివరాలు")) },
                        minLines = 2,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AlphaPrimaryButton(
                        text = BilingualContent.t(language, "Generate Deep Ad Strategy", "లోతైన AI స్క్రిప్ట్ తయారు చేయండి"),
                        onClick = {
                            onRunDeepStrategy(
                                stratBusiness,
                                stratProduct,
                                stratAdType,
                                stratLang,
                                stratDuration,
                                stratNotes
                            )
                        },
                        icon = Icons.Filled.Psychology,
                        useRedAccent = true,
                        enabled = aiStrategyState !is AiStrategyState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_deep_thinking_button")
                    )

                    when (aiStrategyState) {
                        AiStrategyState.Idle -> Unit
                        AiStrategyState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CinematicRed,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Thinking deeply about story hooks, Telugu script & storyboard...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricBlueBright
                                )
                            }
                        }
                        is AiStrategyState.Error -> {
                            Surface(
                                color = CrimsonContainer,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CrimsonError)
                            ) {
                                Text(
                                    aiStrategyState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IvoryWhite,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        is AiStrategyState.Streaming, is AiStrategyState.Success -> {
                            val thoughts = when (aiStrategyState) {
                                is AiStrategyState.Streaming -> aiStrategyState.thoughts
                                is AiStrategyState.Success -> aiStrategyState.thoughts
                            }
                            val answer = when (aiStrategyState) {
                                is AiStrategyState.Streaming -> aiStrategyState.answer
                                is AiStrategyState.Success -> aiStrategyState.answer
                            }
                            val tokenCount = (aiStrategyState as? AiStrategyState.Success)?.thoughtsTokenCount

                            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))

                            if (thoughts.isNotBlank()) {
                                Surface(
                                    color = NavyElevated,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showThoughts = !showThoughts }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "AI High-Thinking Reasoning" +
                                                    (tokenCount?.let { " ($it tokens)" } ?: ""),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = ElectricBlueBright,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = if (showThoughts) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                                contentDescription = null,
                                                tint = ElectricBlueBright
                                            )
                                        }
                                        AnimatedVisibility(visible = showThoughts) {
                                            Text(
                                                text = thoughts,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SlateSilver,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (answer.isNotBlank()) {
                                Text(
                                    text = answer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IvoryWhite
                                )
                            }

                            if (aiStrategyState is AiStrategyState.Success) {
                                AlphaOutlineButton(
                                    text = BilingualContent.t(
                                        language,
                                        "Use Brief in Booking Form",
                                        "ఈ బ్రీఫ్‌ను బుకింగ్ ఫారంలో వాడండి"
                                    ),
                                    onClick = {
                                        onUseStrategyInBooking(
                                            stratBusiness,
                                            stratProduct,
                                            stratAdType,
                                            stratLang,
                                            stratDuration,
                                            answer
                                        )
                                    },
                                    icon = Icons.Filled.AutoAwesome,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AlphaBrandFooter(language = language)
        }
    }
}
