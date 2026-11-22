package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaCard
import com.example.ui.components.AlphaMediaLogo
import com.example.ui.components.AlphaOutlineButton
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.openEmailEnquiry
import com.example.ui.components.openWhatsAppEnquiry
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    language: AppLanguage,
    userDisplayName: String,
    userEmail: String,
    listState: LazyListState = rememberLazyListState(),
    onExploreServices: () -> Unit,
    onViewPortfolio: () -> Unit,
    onBookAd: () -> Unit,
    onSubmitEnquiry: (name: String, business: String, whatsapp: String, email: String, msg: String) -> Unit
) {
    val context = LocalContext.current
    var expandedTermIndex by remember { mutableStateOf<Int?>(0) }

    var enqName by remember { mutableStateOf(userDisplayName) }
    var enqBusiness by remember { mutableStateOf("") }
    var enqWhatsapp by remember { mutableStateOf("") }
    var enqEmail by remember { mutableStateOf(userEmail) }
    var enqMessage by remember { mutableStateOf("") }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StudioBlack, NavyRoyal, StudioBlack))
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. CINEMATIC HERO BANNER WITH OFFICIAL ALPHA MEDIA LOGO
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NavySurface,
                border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.75f), CinematicRed.copy(alpha = 0.75f)))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(225.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_studio),
                            contentDescription = "ALPHA MEDIA AI Advertising Studio",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            StudioBlack.copy(alpha = 0.35f),
                                            NavyMidnight.copy(alpha = 0.85f),
                                            StudioBlack.copy(alpha = 0.95f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AlphaMediaLogo(
                                modifier = Modifier.widthIn(max = 175.dp).fillMaxWidth(0.52f),
                                cornerRadius = 12.dp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 5.dp
                            )
                            Text(
                                text = "MAKE YOUR BUSINESS STAND OUT!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = IvoryWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "మీ వ్యాపారానికి డిజిటల్ గుర్తింపు తీసుకురండి!",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricBlueBright
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = BilingualContent.t(
                                language,
                                "AI-Powered Advertising. Creative Storytelling. ${BilingualContent.SLOGAN_EN}",
                                "AI ఆధారిత ప్రకటనలు. సృజనాత్మక కథనం. ${BilingualContent.SLOGAN_TE}"
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = IvoryWhite
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "Built for local shops, restaurants, dairy brands, clothing stores, startups & small businesses across Andhra Pradesh.",
                                "స్థానిక దుకాణాలు, రెస్టారెంట్లు, డైరీ వ్యాపారాలు, వస్త్ర దుకాణాలు, స్టార్టప్‌లు మరియు చిన్న వ్యాపారాల కోసం ప్రత్యేకంగా రూపొందించబడింది."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AlphaPrimaryButton(
                                text = BilingualContent.t(language, "BOOK YOUR AI AD", "మీ AI ప్రకటన బుక్ చేయండి"),
                                onClick = onBookAd,
                                icon = Icons.Filled.AutoAwesome,
                                useRedAccent = true,
                                modifier = Modifier.testTag("home_book_ad_button")
                            )
                            AlphaOutlineButton(
                                text = BilingualContent.t(language, "Explore Services", "సేవలు చూడండి"),
                                onClick = onExploreServices,
                                icon = Icons.Filled.MovieCreation,
                                modifier = Modifier.testTag("home_explore_services_button")
                            )
                            AlphaOutlineButton(
                                text = BilingualContent.t(language, "View Portfolio", "పోర్ట్‌ఫోలియో"),
                                onClick = onViewPortfolio,
                                icon = Icons.Filled.VideoLibrary,
                                modifier = Modifier.testTag("home_view_portfolio_button")
                            )
                        }
                    }
                }
            }
        }

        // 2. ABOUT US — ALPHA MEDIA STUDIO
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "ABOUT US", "మా గురించి"),
                title = BilingualContent.t(language, "About ALPHA MEDIA", "ALPHA MEDIA పరిచయం"),
                subtitle = BilingualContent.t(
                    language,
                    "AI Advertising & Creative Production Studio serving businesses from Pithapuram, Andhra Pradesh.",
                    "పిఠాపురం, ఆంధ్రప్రదేశ్ నుండి వ్యాపారాలకు సేవలందిస్తున్న AI ప్రకటన & క్రియేటివ్ ప్రొడక్షన్ స్టూడియో."
                )
            )
            Spacer(Modifier.height(12.dp))
            AlphaCard(
                borderColor = ElectricBlue.copy(alpha = 0.55f),
                containerColor = NavyMidnight
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AlphaMediaLogo(
                        modifier = Modifier.width(106.dp),
                        cornerRadius = 12.dp,
                        horizontalPadding = 6.dp,
                        verticalPadding = 4.dp
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${BilingualContent.BRAND_NAME} — ${BilingualContent.TAGLINE}",
                            style = MaterialTheme.typography.titleSmall,
                            color = IvoryWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "We combine AI video production, Telugu & English voiceovers, cinematic product visuals, and transparent client-approved publishing agreements so local brands shine online.",
                                "స్థానిక వ్యాపారాలు డిజిటల్‌గా రాణించేందుకు AI వీడియో నిర్మాణం, తెలుగు & ఇంగ్లీష్ వాయిస్‌ఓవర్, మరియు పారదర్శక ఒప్పంద విధానంతో మేము పని చేస్తాము."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                    }
                }
            }
        }

        // 3. 10-STEP CLIENT WORKFLOW
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "STUDIO PROCESS", "స్టూడియో ప్రక్రియ"),
                title = BilingualContent.t(language, "10-Step Client Workflow", "10-దశల క్లయింట్ వర్క్‌ఫ్లో"),
                subtitle = BilingualContent.t(
                    language,
                    "Transparent stage-by-stage production from brief submission to separate publishing consent.",
                    "బుకింగ్ నుండి వేరు ప్రచురణ అనుమతి వరకు పారదర్శక నిర్మాణ విధానం."
                )
            )
            Spacer(Modifier.height(12.dp))
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BilingualContent.workflowSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (idx % 2 == 0) ElectricBlue else CinematicRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${step.stepNumber}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IvoryWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = BilingualContent.t(language, step.titleEn, step.titleTe),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = IvoryWhite
                                )
                                Text(
                                    text = BilingualContent.t(language, step.detailEn, step.detailTe),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateSilver
                                )
                            }
                        }
                        if (idx < BilingualContent.workflowSteps.lastIndex) {
                            HorizontalDivider(color = NavyBorder.copy(alpha = 0.35f))
                        }
                    }
                }
            }
        }

        // 4. ADVERTISING RIGHTS & TERMS
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "TRUST & GOVERNANCE", "హక్కులు & నిబంధనలు"),
                title = BilingualContent.t(language, "Advertising Rights & Terms", "ప్రకటనల హక్కులు మరియు నిబంధనలు"),
                subtitle = BilingualContent.t(
                    language,
                    "Clear ownership, AI disclosures, and privacy protections.",
                    "స్పష్టమైన యాజమాన్యం, AI ప్రకటనలు మరియు గోప్యతా రక్షణ."
                )
            )
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BilingualContent.legalTermsList.forEachIndexed { index, term ->
                    val expanded = expandedTermIndex == index
                    AlphaCard(
                        modifier = Modifier.clickable {
                            expandedTermIndex = if (expanded) null else index
                        }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Gavel,
                                        contentDescription = null,
                                        tint = ElectricBlueBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = BilingualContent.t(language, term.titleEn, term.titleTe),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = IvoryWhite
                                    )
                                }
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = ElectricBlueBright
                                )
                            }
                            AnimatedVisibility(visible = expanded) {
                                Text(
                                    text = BilingualContent.t(language, term.bodyEn, term.bodyTe),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateSilver,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. CONTACT PITHAPURAM STUDIO & BUSINESS ENQUIRY FORM
        item {
            SectionHeader(
                eyebrow = BilingualContent.t(language, "CONNECT WITH US", "సంప్రదించండి"),
                title = BilingualContent.t(language, "ALPHA MEDIA Studio Contact", "ALPHA MEDIA స్టూడియో సంప్రదింపు"),
                subtitle = BilingualContent.t(
                    language,
                    BilingualContent.STUDIO_LOCATION_EN,
                    BilingualContent.STUDIO_LOCATION_TE
                )
            )
            Spacer(Modifier.height(12.dp))
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AlphaMediaLogo(
                            modifier = Modifier.width(88.dp),
                            cornerRadius = 10.dp,
                            horizontalPadding = 5.dp,
                            verticalPadding = 3.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = BilingualContent.BRAND_NAME,
                                style = MaterialTheme.typography.titleSmall,
                                color = IvoryWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = BilingualContent.TAGLINE,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricBlueBright
                            )
                        }
                    }

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = CinematicRed)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = BilingualContent.t(
                                language,
                                "Service Location: ${BilingualContent.STUDIO_LOCATION_EN}",
                                "సేవా ప్రాంతం: ${BilingualContent.STUDIO_LOCATION_TE}"
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = IvoryWhite
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Phone, contentDescription = null, tint = ElectricBlueBright)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = BilingualContent.PLACEHOLDER_PHONE_DISPLAY,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = ElectricBlueBright)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${BilingualContent.PLACEHOLDER_EMAIL} (Placeholder)  •  IG: ${BilingualContent.PLACEHOLDER_INSTAGRAM}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AlphaPrimaryButton(
                            text = BilingualContent.t(language, "CONTACT ON WHATSAPP", "వాట్సాప్‌లో సంప్రదించండి"),
                            onClick = {
                                openWhatsAppEnquiry(
                                    context,
                                    "Hello ALPHA MEDIA (Pithapuram), I would like to enquire about an AI advertisement."
                                )
                            },
                            icon = Icons.AutoMirrored.Filled.Chat,
                            useRedAccent = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_whatsapp_button")
                        )
                        AlphaOutlineButton(
                            text = BilingualContent.t(language, "Email Studio", "ఇమెయిల్ చేయండి"),
                            onClick = {
                                openEmailEnquiry(
                                    context,
                                    "ALPHA MEDIA AI Advertisement Enquiry",
                                    "Hello ALPHA MEDIA,\n\nI would like to discuss an AI advertisement project."
                                )
                            },
                            icon = Icons.Filled.Email,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_email_button")
                        )
                    }

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f))

                    Text(
                        text = BilingualContent.t(language, "Business Enquiry Form", "వ్యాపార విచారణ ఫారం"),
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricBlueBright
                    )

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = IvoryWhite,
                        unfocusedTextColor = IvoryWhite,
                        focusedLabelColor = ElectricBlueBright,
                        unfocusedLabelColor = SlateSilver
                    )

                    OutlinedTextField(
                        value = enqName,
                        onValueChange = { enqName = it },
                        label = { Text(BilingualContent.t(language, "Your Name *", "మీ పేరు *")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = enqBusiness,
                        onValueChange = { enqBusiness = it },
                        label = { Text(BilingualContent.t(language, "Business Name *", "వ్యాపారం పేరు *")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = enqWhatsapp,
                        onValueChange = { enqWhatsapp = it },
                        label = { Text(BilingualContent.t(language, "WhatsApp Number *", "వాట్సాప్ నంబర్ *")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = enqEmail,
                        onValueChange = { enqEmail = it },
                        label = { Text(BilingualContent.t(language, "Email (Optional)", "ఇమెయిల్ (ఐచ్ఛికం)")) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = enqMessage,
                        onValueChange = { enqMessage = it },
                        label = { Text(BilingualContent.t(language, "Project / Enquiry Details *", "మీ ప్రకటన అవసరాలు *")) },
                        minLines = 3,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AlphaPrimaryButton(
                        text = BilingualContent.t(language, "Send Business Enquiry", "విచారణ పంపండి"),
                        onClick = {
                            onSubmitEnquiry(enqName, enqBusiness, enqWhatsapp, enqEmail, enqMessage)
                            enqMessage = ""
                        },
                        icon = Icons.AutoMirrored.Filled.Send,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_enquiry_button")
                    )
                }
            }
        }

        // 6. OFFICIAL BRAND FOOTER
        item {
            AlphaBrandFooter(language = language)
        }
    }
}
