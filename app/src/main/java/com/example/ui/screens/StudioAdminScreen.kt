package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BilingualContent
import com.example.model.AgencyService
import com.example.model.AppLanguage
import com.example.model.BookingAgreement
import com.example.model.BookingHistoryEvent
import com.example.model.BusinessEnquiry
import com.example.model.PaymentStatus
import com.example.model.PortfolioItem
import com.example.model.ProjectStatus
import com.example.model.PublishingStatus
import com.example.model.UiState
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaCard
import com.example.ui.components.AlphaLogoBannerHeader
import com.example.ui.components.AlphaOutlineButton
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.components.PublishingPill
import com.example.ui.components.StatusPill
import com.example.ui.components.openPdfUri
import com.example.ui.components.openWhatsAppEnquiry
import com.example.ui.components.sharePdfUri
import com.example.ui.theme.CinematicRed
import com.example.ui.theme.CinematicRedBright
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import com.example.util.AgreementPdfGenerator

@Composable
fun StudioAdminScreen(
    language: AppLanguage,
    isSignedIn: Boolean = true,
    isAdmin: Boolean,
    preferredTab: Int = 0,
    myBookingsState: UiState<List<BookingAgreement>>,
    adminBookingsState: UiState<List<BookingAgreement>>,
    enquiriesState: UiState<List<BusinessEnquiry>>,
    portfolioState: UiState<List<PortfolioItem>>,
    servicesState: UiState<List<AgencyService>>,
    historyState: UiState<List<BookingHistoryEvent>>,
    onOpenLogin: () -> Unit = {},
    onBookWithoutLogin: () -> Unit = {},
    onSelectBookingForHistory: (BookingAgreement?) -> Unit,
    onUpdatePublishingRights: (BookingAgreement, Boolean, Boolean, Boolean, Boolean) -> Unit,
    onSubmitClientApproval: (BookingAgreement, Boolean, Boolean, String, String) -> Unit,
    onAdminUpdateBooking: (BookingAgreement, ProjectStatus, PublishingStatus, PaymentStatus, String, String, String, String, Boolean) -> Unit,
    onAdminMarkEnquiryResponded: (String) -> Unit,
    onAdminAddPortfolioItem: (String, String, String, String, String, String, String, String) -> Unit,
    onAdminDeletePortfolioItem: (String) -> Unit,
    onAdminAddService: (Long, String, String, String, String, String, String, String) -> Unit,
    onAdminDeleteService: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var topTab by remember { mutableIntStateOf(if (isAdmin) preferredTab.coerceIn(0, 1) else 0) }
    LaunchedEffect(preferredTab, isAdmin) {
        topTab = if (isAdmin) preferredTab.coerceIn(0, 1) else 0
    }

    var historyBooking by remember { mutableStateOf<BookingAgreement?>(null) }
    var rightsEditBooking by remember { mutableStateOf<BookingAgreement?>(null) }
    var adminEditBooking by remember { mutableStateOf<BookingAgreement?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StudioBlack, NavyRoyal, StudioBlack))
            )
    ) {
        if (!isSignedIn) {
            val guestContext = LocalContext.current
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AlphaLogoBannerHeader(
                        eyebrow = BilingualContent.t(language, "CLIENT & ADMIN PORTAL", "క్లయింట్ & అడ్మిన్ పోర్టల్"),
                        title = BilingualContent.t(language, "Sign In to Manage Bookings", "బుకింగ్స్ నిర్వహించడానికి లాగిన్ అవ్వండి"),
                        subtitle = BilingualContent.t(
                            language,
                            "Use Mobile OTP, optional Email & Password, or Admin Studio credentials.",
                            "మొబైల్ OTP, ఇమెయిల్ & పాస్‌వర్డ్ లేదా అడ్మిన్ వివరాలతో లాగిన్ అవ్వండి."
                        )
                    )
                }
                item {
                    AlphaCard(borderColor = ElectricBlue.copy(alpha = 0.7f)) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = BilingualContent.t(
                                    language,
                                    "Your ALPHA MEDIA Account",
                                    "మీ ALPHA MEDIA ఖాతా"
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricBlueBright,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = BilingualContent.t(
                                    language,
                                    "Sign in to track your bookings, request revisions, download signed PDFs, or open the protected Admin Dashboard. Want to book a new ad first? No login is required!",
                                    "మీ బుకింగ్ స్థితిని చూడటానికి, సవరణలు అభ్యర్థించడానికి లేదా అడ్మిన్ డ్యాష్‌బోర్డ్ తెరవడానికి లాగిన్ అవ్వండి. కొత్త బుకింగ్ కోసం లాగిన్ అవసరం లేదు!"
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateSilver
                            )
                            AlphaPrimaryButton(
                                text = BilingualContent.t(language, "SIGN IN (MOBILE / EMAIL / ADMIN)", "లాగిన్ (మొబైల్ / ఇమెయిల్ / అడ్మిన్)"),
                                onClick = onOpenLogin,
                                icon = Icons.Filled.AdminPanelSettings,
                                modifier = Modifier.fillMaxWidth().testTag("studio_open_login_button")
                            )
                            AlphaPrimaryButton(
                                text = BilingualContent.t(language, "BOOK AN AD WITHOUT LOGIN", "లాగిన్ లేకుండా ప్రకటన బుక్ చేయండి"),
                                onClick = onBookWithoutLogin,
                                useRedAccent = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            AlphaOutlineButton(
                                text = BilingualContent.t(language, "Contact ALPHA MEDIA on WhatsApp", "WhatsApp లో సంప్రదించండి"),
                                onClick = {
                                    openWhatsAppEnquiry(
                                        guestContext,
                                        "Hello ALPHA MEDIA! I need help with my booking status or account."
                                    )
                                },
                                icon = Icons.AutoMirrored.Filled.Chat,
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
            if (isAdmin) {
                TabRow(
                    selectedTabIndex = topTab,
                    containerColor = StudioBlack,
                    contentColor = ElectricBlueBright,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[topTab]),
                            color = CinematicRed
                        )
                    }
                ) {
                    Tab(
                        selected = topTab == 0,
                        onClick = { topTab = 0 },
                        text = { Text(BilingualContent.t(language, "Client Dashboard", "క్లయింట్ డ్యాష్‌బోర్డ్"), fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = topTab == 1,
                        onClick = { topTab = 1 },
                        modifier = Modifier.testTag("tab_admin_dashboard"),
                        text = { Text(BilingualContent.t(language, "Admin Dashboard", "అడ్మిన్ డ్యాష్‌బోర్డ్"), fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (!isAdmin || topTab == 0) {
                ClientBookingsList(
                    language = language,
                    bookingsState = myBookingsState,
                    onOpenHistory = { b ->
                        historyBooking = b
                        onSelectBookingForHistory(b)
                    },
                    onEditPublishingRights = { rightsEditBooking = it },
                    onSubmitClientApproval = onSubmitClientApproval,
                    onShowMessage = onShowMessage
                )
            } else {
                AdminDashboardContent(
                    language = language,
                    adminBookingsState = adminBookingsState,
                    enquiriesState = enquiriesState,
                    portfolioState = portfolioState,
                    servicesState = servicesState,
                    onOpenHistory = { b ->
                        historyBooking = b
                        onSelectBookingForHistory(b)
                    },
                    onOpenAdminEdit = { adminEditBooking = it },
                    onMarkEnquiryResponded = onAdminMarkEnquiryResponded,
                    onAddPortfolio = onAdminAddPortfolioItem,
                    onDeletePortfolio = onAdminDeletePortfolioItem,
                    onAddService = onAdminAddService,
                    onDeleteService = onAdminDeleteService,
                    onShowMessage = onShowMessage
                )
            }
        }
    }

    historyBooking?.let { booking ->
        HistoryModal(
            booking = booking,
            historyState = historyState,
            onDismiss = {
                historyBooking = null
                onSelectBookingForHistory(null)
            }
        )
    }

    rightsEditBooking?.let { booking ->
        PublishingRightsEditDialog(
            language = language,
            booking = booking,
            onDismiss = { rightsEditBooking = null },
            onSave = { cSoc, aSoc, aPort, doNot ->
                onUpdatePublishingRights(booking, cSoc, aSoc, aPort, doNot)
                rightsEditBooking = null
            }
        )
    }

    adminEditBooking?.let { booking ->
        AdminEditBookingDialog(
            booking = booking,
            onDismiss = { adminEditBooking = null },
            onSave = { proj, pub, pay, price, adv, rep, rev, deliv ->
                onAdminUpdateBooking(booking, proj, pub, pay, price, adv, rep, rev, deliv)
                adminEditBooking = null
            }
        )
    }
}

@Composable
private fun ClientBookingsList(
    language: AppLanguage,
    bookingsState: UiState<List<BookingAgreement>>,
    onOpenHistory: (BookingAgreement) -> Unit,
    onEditPublishingRights: (BookingAgreement) -> Unit,
    onSubmitClientApproval: (BookingAgreement, Boolean, Boolean, String, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AlphaLogoBannerHeader(
                eyebrow = BilingualContent.t(language, "CLIENT DASHBOARD", "క్లయింట్ డ్యాష్‌బోర్డ్"),
                title = BilingualContent.t(language, "My Bookings & Agreements", "నా బుకింగ్స్ & ఒప్పందాలు"),
                subtitle = BilingualContent.t(
                    language,
                    "Track production status, download PDFs, or withdraw publishing rights anytime.",
                    "నిర్మాణ స్థితిని చూడండి, PDF డౌన్‌లోడ్ చేయండి లేదా ప్రచురణ అనుమతిని ఎప్పుడైనా ఉపసంహరించండి."
                )
            )
        }

        when (bookingsState) {
            UiState.Loading -> item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = ElectricBlueBright)
                }
            }
            is UiState.Error -> item {
                AlphaCard(borderColor = CrimsonError, containerColor = CrimsonContainer) {
                    Text(bookingsState.message, color = IvoryWhite, modifier = Modifier.padding(16.dp))
                }
            }
            is UiState.Success -> {
                if (bookingsState.data.isEmpty()) {
                    item {
                        AlphaCard {
                            Text(
                                text = BilingualContent.t(
                                    language,
                                    "No bookings yet. Use the Book & Agree tab to submit your first AI advertisement request.",
                                    "ఇంకా బుకింగ్స్ లేవు. మీ మొదటి AI ప్రకటన కోసం Book & Agree ట్యాబ్‌ను ఉపయోగించండి."
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateSilver,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }
                } else {
                    items(bookingsState.data, key = { it.id }) { booking ->
                        var revisionInput by remember(booking.id) { mutableStateOf(booking.revisionNotes) }
                        AlphaCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = booking.referenceNumber,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ElectricBlueBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                    StatusPill(ProjectStatus.fromKey(booking.projectStatus), language)
                                }
                                Text(booking.adTitle, style = MaterialTheme.typography.titleLarge, color = IvoryWhite)
                                Text(
                                    "${booking.businessName} • ${booking.adType} • ${booking.adLanguage} • v${booking.agreementVersion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateSilver
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Publishing:", style = MaterialTheme.typography.labelSmall, color = SlateSilver)
                                    PublishingPill(PublishingStatus.fromKey(booking.publishingStatus), language)
                                }
                                Text(
                                    "Payment: ${PaymentStatus.fromKey(booking.paymentStatus).labelEn} | Price: ${booking.agreedPrice.ifBlank { booking.budgetRange }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricBlueBright
                                )

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AlphaOutlineButton(
                                        text = "PDF",
                                        onClick = {
                                            AgreementPdfGenerator.generateAgreementPdf(context, booking).fold(
                                                onSuccess = { openPdfUri(context, it) },
                                                onFailure = { onShowMessage(it.localizedMessage ?: "PDF failed") }
                                            )
                                        },
                                        icon = Icons.Filled.PictureAsPdf,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AlphaOutlineButton(
                                        text = BilingualContent.t(language, "Share", "పంపండి"),
                                        onClick = {
                                            AgreementPdfGenerator.generateAgreementPdf(context, booking).fold(
                                                onSuccess = {
                                                    sharePdfUri(context, it, "Agreement ${booking.agreementReferenceNumber}", booking.adTitle)
                                                },
                                                onFailure = { onShowMessage(it.localizedMessage ?: "Share failed") }
                                            )
                                        },
                                        icon = Icons.Filled.Share,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AlphaOutlineButton(
                                        text = BilingualContent.t(language, "History", "చరిత్ర"),
                                        onClick = { onOpenHistory(booking) },
                                        icon = Icons.Filled.History,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AlphaPrimaryButton(
                                        text = BilingualContent.t(language, "Publishing Rights", "ప్రచురణ హక్కులు"),
                                        onClick = { onEditPublishingRights(booking) },
                                        icon = Icons.Filled.Security,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AlphaOutlineButton(
                                        text = "WhatsApp",
                                        onClick = {
                                            openWhatsAppEnquiry(
                                                context,
                                                "ALPHA MEDIA Follow-up: Booking ${booking.referenceNumber} (${booking.adTitle})."
                                            )
                                        },
                                        icon = Icons.AutoMirrored.Filled.Chat,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (booking.projectStatus != ProjectStatus.DELIVERED.key) {
                                    OutlinedTextField(
                                        value = revisionInput,
                                        onValueChange = { revisionInput = it },
                                        label = { Text(BilingualContent.t(language, "Correction / Revision Note", "సవరణ / మార్పు సూచన")) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricBlue,
                                            unfocusedBorderColor = NavyBorder,
                                            focusedTextColor = IvoryWhite,
                                            unfocusedTextColor = IvoryWhite
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AlphaOutlineButton(
                                            text = BilingualContent.t(language, "Request Correction", "సవరణ కోరండి"),
                                            onClick = {
                                                onSubmitClientApproval(
                                                    booking,
                                                    booking.clientFinalApproved,
                                                    booking.clientApprovalAccuracy,
                                                    revisionInput,
                                                    booking.specialInstructions
                                                )
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (!booking.clientFinalApproved) {
                                            AlphaPrimaryButton(
                                                text = BilingualContent.t(language, "Approve Final Ad", "తుది ఆమోదం"),
                                                onClick = {
                                                    onSubmitClientApproval(
                                                        booking,
                                                        true,
                                                        true,
                                                        revisionInput,
                                                        booking.specialInstructions
                                                    )
                                                },
                                                useRedAccent = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
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

@Composable
private fun AdminDashboardContent(
    language: AppLanguage,
    adminBookingsState: UiState<List<BookingAgreement>>,
    enquiriesState: UiState<List<BusinessEnquiry>>,
    portfolioState: UiState<List<PortfolioItem>>,
    servicesState: UiState<List<AgencyService>>,
    onOpenHistory: (BookingAgreement) -> Unit,
    onOpenAdminEdit: (BookingAgreement) -> Unit,
    onMarkEnquiryResponded: (String) -> Unit,
    onAddPortfolio: (String, String, String, String, String, String, String, String) -> Unit,
    onDeletePortfolio: (String) -> Unit,
    onAddService: (Long, String, String, String, String, String, String, String) -> Unit,
    onDeleteService: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var adminSubTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Bookings", "Enquiries", "Portfolio & Services").forEachIndexed { idx, label ->
                FilterChip(
                    selected = adminSubTab == idx,
                    onClick = { adminSubTab = idx },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = IvoryWhite,
                        containerColor = NavySurface,
                        labelColor = IvoryWhite
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AlphaLogoBannerHeader(
                    eyebrow = BilingualContent.t(language, "PROTECTED ADMIN CONSOLE", "రక్షిత అడ్మిన్ కన్సోల్"),
                    title = BilingualContent.t(language, "ALPHA MEDIA Admin Dashboard", "ALPHA MEDIA అడ్మిన్ డ్యాష్‌బోర్డ్"),
                    subtitle = BilingualContent.t(
                        language,
                        "Manage bookings, project status, publishing rights, enquiries, and studio showcase.",
                        "బుకింగ్స్, ప్రాజెక్ట్ స్థితి, ప్రచురణ హక్కులు మరియు పోర్ట్‌ఫోలియోను నిర్వహించండి."
                    )
                )
            }

            if (adminSubTab == 0) {
                item {
                    Surface(
                        color = CrimsonContainer,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CrimsonError),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = CinematicRedBright)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "ADMIN PUBLISHING RULE: Never publish a client's advertisement if Publishing Status is NOT AUTHORIZED or REVOKED.",
                                style = MaterialTheme.typography.bodySmall,
                                color = IvoryWhite
                            )
                        }
                    }
                }
                when (adminBookingsState) {
                    UiState.Loading -> item { CircularProgressIndicator(color = ElectricBlueBright) }
                    is UiState.Error -> item { Text(adminBookingsState.message, color = CrimsonError) }
                    is UiState.Success -> {
                        items(adminBookingsState.data, key = { it.id }) { b ->
                            AlphaCard {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${b.referenceNumber} • ${b.clientFullName}", style = MaterialTheme.typography.titleSmall, color = ElectricBlueBright)
                                        StatusPill(ProjectStatus.fromKey(b.projectStatus), language)
                                    }
                                    Text("${b.businessName} | ${b.whatsappNumber} | ${b.adTitle}", style = MaterialTheme.typography.bodyMedium, color = IvoryWhite)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        PublishingPill(PublishingStatus.fromKey(b.publishingStatus), language)
                                        Text("Pay: ${b.paymentStatus} | Approved: ${b.clientFinalApproved}", style = MaterialTheme.typography.labelSmall, color = SlateSilver)
                                    }
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AlphaPrimaryButton(
                                            text = "Manage Status",
                                            onClick = { onOpenAdminEdit(b) },
                                            icon = Icons.Filled.Edit,
                                            modifier = Modifier.weight(1f)
                                        )
                                        AlphaOutlineButton(
                                            text = "PDF",
                                            onClick = {
                                                AgreementPdfGenerator.generateAgreementPdf(context, b).fold(
                                                    onSuccess = { openPdfUri(context, it) },
                                                    onFailure = { onShowMessage(it.localizedMessage ?: "PDF error") }
                                                )
                                            },
                                            icon = Icons.Filled.PictureAsPdf,
                                            modifier = Modifier.weight(0.7f)
                                        )
                                        AlphaOutlineButton(
                                            text = "Log",
                                            onClick = { onOpenHistory(b) },
                                            icon = Icons.Filled.History,
                                            modifier = Modifier.weight(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (adminSubTab == 1) {
                when (enquiriesState) {
                    UiState.Loading -> item { CircularProgressIndicator(color = ElectricBlueBright) }
                    is UiState.Error -> item { Text(enquiriesState.message, color = CrimsonError) }
                    is UiState.Success -> {
                        items(enquiriesState.data, key = { it.id }) { enq ->
                            AlphaCard {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("${enq.businessName} (${enq.name})", style = MaterialTheme.typography.titleSmall, color = ElectricBlueBright)
                                    Text("WhatsApp: ${enq.whatsappNumber} | Status: ${enq.status}", style = MaterialTheme.typography.labelSmall, color = SlateSilver)
                                    Text(enq.message, style = MaterialTheme.typography.bodyMedium, color = IvoryWhite)
                                    if (enq.status == "OPEN") {
                                        AlphaPrimaryButton(
                                            text = "Mark Responded",
                                            onClick = { onMarkEnquiryResponded(enq.id) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    AdminPortfolioServiceManager(
                        portfolioState = portfolioState,
                        servicesState = servicesState,
                        onAddPortfolio = onAddPortfolio,
                        onDeletePortfolio = onDeletePortfolio,
                        onAddService = onAddService,
                        onDeleteService = onDeleteService
                    )
                }
            }

            item {
                AlphaBrandFooter(language = language)
            }
        }
    }
}

@Composable
private fun AdminPortfolioServiceManager(
    portfolioState: UiState<List<PortfolioItem>>,
    servicesState: UiState<List<AgencyService>>,
    onAddPortfolio: (String, String, String, String, String, String, String, String) -> Unit,
    onDeletePortfolio: (String) -> Unit,
    onAddService: (Long, String, String, String, String, String, String, String) -> Unit,
    onDeleteService: (String) -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricBlue,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = IvoryWhite,
        unfocusedTextColor = IvoryWhite,
        focusedLabelColor = ElectricBlueBright,
        unfocusedLabelColor = SlateSilver
    )

    var pTitleEn by remember { mutableStateOf("") }
    var pTitleTe by remember { mutableStateOf("") }
    var pCat by remember { mutableStateOf("Product Advertisements") }
    var pDescEn by remember { mutableStateOf("") }
    var pStoryboard by remember { mutableStateOf("Opening hook\nProduct close-up\nWhatsApp CTA") }

    var sTitleEn by remember { mutableStateOf("") }
    var sTitleTe by remember { mutableStateOf("") }
    var sDescEn by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AlphaCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Portfolio Concept Item", style = MaterialTheme.typography.titleMedium, color = ElectricBlueBright)
                OutlinedTextField(value = pTitleEn, onValueChange = { pTitleEn = it }, label = { Text("Title (English)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pTitleTe, onValueChange = { pTitleTe = it }, label = { Text("Title (Telugu)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pCat, onValueChange = { pCat = it }, label = { Text("Category") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pDescEn, onValueChange = { pDescEn = it }, label = { Text("Description") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pStoryboard, onValueChange = { pStoryboard = it }, label = { Text("Storyboard Lines") }, minLines = 2, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                AlphaPrimaryButton(
                    text = "Publish Portfolio Item",
                    onClick = {
                        onAddPortfolio(pTitleEn, pTitleTe, pCat, pDescEn, pDescEn, "30s Concept", "product", pStoryboard)
                        pTitleEn = ""; pTitleTe = ""; pDescEn = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                val remotePortfolio = ((portfolioState as? UiState.Success)?.data ?: emptyList()).filter { !it.isBuiltIn }
                remotePortfolio.forEach { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.titleEn, color = IvoryWhite, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onDeletePortfolio(item.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CrimsonError)
                        }
                    }
                }
            }
        }

        AlphaCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Agency Service", style = MaterialTheme.typography.titleMedium, color = ElectricBlueBright)
                OutlinedTextField(value = sTitleEn, onValueChange = { sTitleEn = it }, label = { Text("Service Title (English)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sTitleTe, onValueChange = { sTitleTe = it }, label = { Text("Service Title (Telugu)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sDescEn, onValueChange = { sDescEn = it }, label = { Text("Description") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                AlphaPrimaryButton(
                    text = "Save Service",
                    onClick = {
                        onAddService(11L, sTitleEn, sTitleTe, sDescEn, sDescEn, "Custom AI Pack", "కస్టమ్ AI ప్యాక్", "AI Video Advertisement")
                        sTitleEn = ""; sTitleTe = ""; sDescEn = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                val remoteServices = ((servicesState as? UiState.Success)?.data ?: emptyList()).filter { it.createdBy.isNotBlank() }
                remoteServices.forEach { srv ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(srv.titleEn, color = IvoryWhite, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onDeleteService(srv.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CrimsonError)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishingRightsEditDialog(
    language: AppLanguage,
    booking: BookingAgreement,
    onDismiss: () -> Unit,
    onSave: (Boolean, Boolean, Boolean, Boolean) -> Unit
) {
    var cSoc by remember { mutableStateOf(booking.publishClientSocial) }
    var aSoc by remember { mutableStateOf(booking.publishAgencySocial) }
    var aPort by remember { mutableStateOf(booking.publishAgencyPortfolio) }
    var doNot by remember { mutableStateOf(booking.publishDoNotPublish) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(color = NavySurface, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, ElectricBlue)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    BilingualContent.t(language, "Manage Publishing Rights", "ప్రచురణ హక్కులను మార్చండి"),
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricBlueBright
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = cSoc, onCheckedChange = { cSoc = it; if (it) doNot = false }, colors = CheckboxDefaults.colors(checkedColor = ElectricBlue))
                    Text("Client Social Media", color = IvoryWhite, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aSoc, onCheckedChange = { aSoc = it; if (it) doNot = false }, colors = CheckboxDefaults.colors(checkedColor = ElectricBlue))
                    Text("ALPHA MEDIA Social Media", color = IvoryWhite, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aPort, onCheckedChange = { aPort = it; if (it) doNot = false }, colors = CheckboxDefaults.colors(checkedColor = ElectricBlue))
                    Text("ALPHA MEDIA Portfolio", color = IvoryWhite, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = doNot, onCheckedChange = { doNot = it; if (it) { cSoc = false; aSoc = false; aPort = false } }, colors = CheckboxDefaults.colors(checkedColor = CrimsonError))
                    Text("Withdraw / Do Not Publish Publicly", color = IvoryWhite, style = MaterialTheme.typography.bodySmall)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AlphaOutlineButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f))
                    AlphaPrimaryButton(text = "Save Rights", onClick = { onSave(cSoc, aSoc, aPort, doNot) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminEditBookingDialog(
    booking: BookingAgreement,
    onDismiss: () -> Unit,
    onSave: (ProjectStatus, PublishingStatus, PaymentStatus, String, String, String, String, Boolean) -> Unit
) {
    var proj by remember { mutableStateOf(ProjectStatus.fromKey(booking.projectStatus)) }
    var pub by remember { mutableStateOf(PublishingStatus.fromKey(booking.publishingStatus)) }
    var pay by remember { mutableStateOf(PaymentStatus.fromKey(booking.paymentStatus)) }
    var price by remember { mutableStateOf(booking.agreedPrice) }
    var adv by remember { mutableStateOf(booking.advancePaymentDetails) }
    var rep by remember { mutableStateOf(booking.representativeName) }
    var rev by remember { mutableStateOf(booking.revisionNotes) }
    var deliv by remember { mutableStateOf(booking.deliveryCompleted) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, ElectricBlue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ElectricBlueBright)
                        Spacer(Modifier.width(8.dp))
                        Text("Admin Manage: ${booking.referenceNumber}", style = MaterialTheme.typography.titleMedium, color = ElectricBlueBright)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close", tint = IvoryWhite) }
                }

                Text("Project Status", style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ProjectStatus.entries.forEach { st ->
                        FilterChip(
                            selected = proj == st,
                            onClick = { proj = st },
                            label = { Text(st.labelEn) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue, selectedLabelColor = IvoryWhite)
                        )
                    }
                }

                Text("Publishing Status", style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PublishingStatus.entries.forEach { ps ->
                        FilterChip(
                            selected = pub == ps,
                            onClick = { pub = ps },
                            label = { Text(ps.labelEn) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue, selectedLabelColor = IvoryWhite)
                        )
                    }
                }

                Text("Payment Status", style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaymentStatus.entries.forEach { py ->
                        FilterChip(
                            selected = pay == py,
                            onClick = { pay = py },
                            label = { Text(py.labelEn) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue, selectedLabelColor = IvoryWhite)
                        )
                    }
                }

                val fc = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = IvoryWhite,
                    unfocusedTextColor = IvoryWhite
                )
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Agreed Price") }, colors = fc, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = adv, onValueChange = { adv = it }, label = { Text("Advance Payment Details") }, colors = fc, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = rep, onValueChange = { rep = it }, label = { Text("Representative Name") }, colors = fc, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = rev, onValueChange = { rev = it }, label = { Text("Revision / Delivery Notes") }, colors = fc, modifier = Modifier.fillMaxWidth())

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Mark Final Delivery Completed", color = IvoryWhite)
                    Switch(
                        checked = deliv,
                        onCheckedChange = { deliv = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = IvoryWhite, checkedTrackColor = CinematicRed)
                    )
                }

                AlphaPrimaryButton(
                    text = "Save Admin Update",
                    onClick = { onSave(proj, pub, pay, price, adv, rep, rev, deliv) },
                    useRedAccent = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HistoryModal(
    booking: BookingAgreement,
    historyState: UiState<List<BookingHistoryEvent>>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = NavySurface, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, ElectricBlue)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Version History: ${booking.referenceNumber}", style = MaterialTheme.typography.titleSmall, color = ElectricBlueBright)
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close", tint = IvoryWhite) }
                }
                HorizontalDivider(color = NavyBorder)
                when (historyState) {
                    UiState.Loading -> CircularProgressIndicator(color = ElectricBlueBright)
                    is UiState.Error -> Text(historyState.message, color = CrimsonError)
                    is UiState.Success -> {
                        if (historyState.data.isEmpty()) {
                            Text("No history events yet.", color = SlateSilver)
                        } else {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                historyState.data.forEach { evt ->
                                    Text(
                                        text = "v${evt.agreementVersion} [${evt.actorRole}] ${evt.actionTitle}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ElectricBlueBright
                                    )
                                    Text(evt.actionDetail, style = MaterialTheme.typography.bodySmall, color = SlateSilver)
                                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.35f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
