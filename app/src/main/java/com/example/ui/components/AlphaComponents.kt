package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.model.ProjectStatus
import com.example.model.PublishingStatus
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPending
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicRedBright
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.SkyInfo
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import java.net.URLEncoder

/**
 * Reusable official ALPHA MEDIA brand logo component.
 * Preserves exact 3:2 proportions, original colors, film reel, play button, video camera,
 * "ALPHA MEDIA" wordmark, and "Create • Edit • Inspire" slogan without cropping or distortion.
 */
@Composable
fun AlphaMediaLogo(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    horizontalPadding: Dp = 10.dp,
    verticalPadding: Dp = 6.dp
) {
    Surface(
        modifier = modifier,
        color = IvoryWhite,
        shape = RoundedCornerShape(cornerRadius),
        shadowElevation = 4.dp,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(ElectricBlue, CinematicRed))
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.alpha_media_logo),
                contentDescription = "ALPHA MEDIA — Create • Edit • Inspire Official Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )
        }
    }
}

@Composable
fun AlphaLogoBannerHeader(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = StudioBlack,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.7f), CinematicRed.copy(alpha = 0.7f)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(StudioBlack, NavyMidnight, NavySurface.copy(alpha = 0.9f))
                    )
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AlphaMediaLogo(
                modifier = Modifier.width(102.dp),
                cornerRadius = 12.dp,
                horizontalPadding = 6.dp,
                verticalPadding = 4.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlueBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = IvoryWhite,
                    fontWeight = FontWeight.Bold
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateSilver
                    )
                }
            }
        }
    }
}

@Composable
fun AlphaBrandFooter(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = StudioBlack,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.65f), CinematicRed.copy(alpha = 0.65f)))
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(NavyMidnight, StudioBlack))
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlphaMediaLogo(
                modifier = Modifier.widthIn(max = 176.dp).fillMaxWidth(0.56f),
                cornerRadius = 14.dp,
                horizontalPadding = 10.dp,
                verticalPadding = 6.dp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${BilingualContent.BRAND_NAME}  •  ${BilingualContent.TAGLINE}",
                style = MaterialTheme.typography.titleSmall,
                color = IvoryWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = BilingualContent.t(language, BilingualContent.SLOGAN_EN, BilingualContent.SLOGAN_TE),
                style = MaterialTheme.typography.bodySmall,
                color = ElectricBlueBright,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
                color = NavyBorder.copy(alpha = 0.45f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = BilingualContent.t(
                    language,
                    "${BilingualContent.STUDIO_LOCATION_EN}  •  YouTube: ${BilingualContent.PLACEHOLDER_YOUTUBE}",
                    "${BilingualContent.STUDIO_LOCATION_TE}  •  YouTube: ${BilingualContent.PLACEHOLDER_YOUTUBE}"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = SlateSilver,
                textAlign = TextAlign.Center
            )
            Text(
                text = BilingualContent.t(
                    language,
                    "AI Advertising & Creative Production Studio • Official Brand Portal",
                    "AI ప్రకటనలు & క్రియేటివ్ ప్రొడక్షన్ స్టూడియో • అధికారిక బ్రాండ్ పోర్టల్"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = CinematicRedBright,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphaTopBar(
    language: AppLanguage,
    isSignedIn: Boolean,
    isAdmin: Boolean,
    userIdentifier: String,
    showMenuButton: Boolean = true,
    onOpenDrawer: () -> Unit = {},
    onToggleLanguage: () -> Unit,
    onOpenLogin: () -> Unit = {},
    onOpenAdminOrBookings: () -> Unit,
    onSignOut: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = StudioBlack,
            titleContentColor = IvoryWhite
        ),
        navigationIcon = {
            if (showMenuButton) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("nav_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Open navigation menu",
                        tint = IvoryWhite
                    )
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = IvoryWhite,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(listOf(ElectricBlue, CinematicRed))
                    ),
                    modifier = Modifier.height(34.dp).aspectRatio(1.5f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.alpha_media_logo),
                        contentDescription = "ALPHA MEDIA Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 3.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = BilingualContent.BRAND_NAME,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvoryWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = BilingualContent.TAGLINE,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricBlueBright,
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            Surface(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("language_toggle_button")
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onToggleLanguage),
                color = NavySurface,
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = "Switch language",
                        tint = ElectricBlueBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (language == AppLanguage.ENGLISH) "EN | తె" else "తె | EN",
                        style = MaterialTheme.typography.labelMedium,
                        color = IvoryWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            if (!isSignedIn) {
                Surface(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("topbar_login_button")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onOpenLogin),
                    color = ElectricBlue,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = BilingualContent.t(language, "Login", "లాగిన్"),
                        style = MaterialTheme.typography.labelMedium,
                        color = IvoryWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            } else {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("account_menu_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isAdmin) CinematicRed else NavyElevated)
                                .border(1.5.dp, ElectricBlueBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userIdentifier.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "A",
                                style = MaterialTheme.typography.labelLarge,
                                color = IvoryWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = NavySurface
                    ) {
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = userIdentifier.ifBlank {
                                            BilingualContent.t(language, "ALPHA MEDIA Client", "ALPHA MEDIA క్లయింట్")
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = IvoryWhite
                                    )
                                    if (isAdmin) {
                                        Text(
                                            text = "Authorized Studio Admin",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CinematicRedBright
                                        )
                                    }
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenAdminOrBookings()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = ElectricBlueBright
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = BilingualContent.t(language, "Sign Out", "సైన్ అవుట్"),
                                    color = CinematicRedBright
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onSignOut()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = CinematicRedBright
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AlphaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    useRedAccent: Boolean = false
) {
    val bg = if (useRedAccent) CinematicRed else ElectricBlue
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.minimumInteractiveComponentSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = IvoryWhite,
            disabledContainerColor = bg.copy(alpha = 0.35f),
            disabledContentColor = IvoryWhite.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AlphaOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.minimumInteractiveComponentSize(),
        border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.85f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = IvoryWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricBlueBright,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = CinematicRedBright,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = ElectricBlueBright,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = IvoryWhite
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusPill(status: ProjectStatus, language: AppLanguage) {
    val (bg, fg) = when (status) {
        ProjectStatus.DELIVERED -> EmeraldContainer to EmeraldSuccess
        ProjectStatus.FINAL_APPROVAL, ProjectStatus.CLIENT_REVIEW -> SkyContainer to SkyInfo
        ProjectStatus.PRODUCTION, ProjectStatus.SCRIPT_APPROVAL -> AmberContainer to AmberPending
        else -> NavyElevated to ElectricBlueBright
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.55f))
    ) {
        Text(
            text = if (language == AppLanguage.TELUGU) status.labelTe else status.labelEn,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PublishingPill(status: PublishingStatus, language: AppLanguage) {
    val (bg, fg) = when (status) {
        PublishingStatus.AUTHORIZED -> EmeraldContainer to EmeraldSuccess
        PublishingStatus.PENDING_APPROVAL -> AmberContainer to AmberPending
        PublishingStatus.REVOKED, PublishingStatus.NOT_AUTHORIZED -> CrimsonContainer to CrimsonError
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.55f))
    ) {
        Text(
            text = if (language == AppLanguage.TELUGU) status.labelTe else status.labelEn,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AlphaCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NavyBorder.copy(alpha = 0.65f),
    containerColor: Color = NavySurface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 2.dp
    ) {
        content()
    }
}

fun openWhatsAppEnquiry(context: Context, message: String) {
    val encoded = URLEncoder.encode(message, "UTF-8")
    val phoneDigits = BilingualContent.PLACEHOLDER_WHATSAPP_NUMBER.replace("+", "")
    val uri = Uri.parse("https://wa.me/$phoneDigits?text=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    runCatching { context.startActivity(intent) }
}

fun openEmailEnquiry(context: Context, subject: String, body: String) {
    val uri = Uri.parse("mailto:${BilingualContent.PLACEHOLDER_EMAIL}")
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    runCatching { context.startActivity(intent) }
}

fun openPdfUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, "Open Agreement PDF"))
    }
}

fun sharePdfUri(context: Context, uri: Uri, subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, "Send Agreement Copy"))
    }
}
