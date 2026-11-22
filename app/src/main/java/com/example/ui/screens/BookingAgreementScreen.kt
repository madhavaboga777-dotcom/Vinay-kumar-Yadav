package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.model.BookingAgreement
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaCard
import com.example.ui.components.AlphaLogoBannerHeader
import com.example.ui.components.AlphaMediaLogo
import com.example.ui.components.AlphaOutlineButton
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.components.openPdfUri
import com.example.ui.components.openWhatsAppEnquiry
import com.example.ui.components.sharePdfUri
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
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import com.example.util.AgreementPdfGenerator
import com.example.viewmodel.BookingFormState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingAgreementScreen(
    language: AppLanguage,
    form: BookingFormState,
    lastSubmittedBooking: BookingAgreement?,
    isSignedIn: Boolean = true,
    onOpenLogin: () -> Unit = {},
    onUpdateForm: ((BookingFormState) -> BookingFormState) -> Unit,
    onSubmitBooking: () -> Unit,
    onClearConfirmation: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showAgreementPreview by remember { mutableStateOf(false) }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val names = uris.map { uri ->
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (it.moveToFirst() && idx >= 0) it.getString(idx) else uri.lastPathSegment
                } ?: uri.lastPathSegment ?: "media_item"
            }
            onUpdateForm { it.copy(referenceMediaNames = names) }
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricBlue,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = IvoryWhite,
        unfocusedTextColor = IvoryWhite,
        focusedLabelColor = ElectricBlueBright,
        unfocusedLabelColor = SlateSilver
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StudioBlack, NavyRoyal, StudioBlack))
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Post-submission confirmation card
        if (lastSubmittedBooking != null) {
            item {
                AlphaCard(borderColor = EmeraldSuccess, containerColor = EmeraldContainer) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = BilingualContent.t(
                                    language,
                                    "Booking & Digital Agreement Confirmed!",
                                    "బుకింగ్ & డిజిటల్ ఒప్పందం నిర్ధారించబడింది!"
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                color = IvoryWhite
                            )
                        }
                        Text(
                            text = "Booking Ref: ${lastSubmittedBooking.referenceNumber}\nAgreement Ref: ${lastSubmittedBooking.agreementReferenceNumber}\nPublishing Status: ${lastSubmittedBooking.publishingStatus}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricBlueBright
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AlphaPrimaryButton(
                                text = BilingualContent.t(language, "DOWNLOAD PDF", "PDF డౌన్‌లోడ్"),
                                onClick = {
                                    AgreementPdfGenerator.generateAgreementPdf(context, lastSubmittedBooking)
                                        .fold(
                                            onSuccess = { uri -> openPdfUri(context, uri) },
                                            onFailure = { onShowMessage(it.localizedMessage ?: "PDF failed") }
                                        )
                                },
                                icon = Icons.Filled.PictureAsPdf,
                                modifier = Modifier.weight(1f)
                            )
                            AlphaOutlineButton(
                                text = BilingualContent.t(language, "Send Copy", "కాపీ పంపండి"),
                                onClick = {
                                    AgreementPdfGenerator.generateAgreementPdf(context, lastSubmittedBooking)
                                        .fold(
                                            onSuccess = { uri ->
                                                sharePdfUri(
                                                    context,
                                                    uri,
                                                    "ALPHA MEDIA Agreement ${lastSubmittedBooking.agreementReferenceNumber}",
                                                    "ALPHA MEDIA Booking ${lastSubmittedBooking.referenceNumber}"
                                                )
                                            },
                                            onFailure = { onShowMessage(it.localizedMessage ?: "Share failed") }
                                        )
                                },
                                icon = Icons.Filled.Share,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        AlphaPrimaryButton(
                            text = BilingualContent.t(language, "CONTACT ON WHATSAPP", "వాట్సాప్‌లో సంప్రదించండి"),
                            onClick = {
                                openWhatsAppEnquiry(
                                    context,
                                    "Hello ALPHA MEDIA, I submitted booking ${lastSubmittedBooking.referenceNumber} (Agreement ${lastSubmittedBooking.agreementReferenceNumber}) for ${lastSubmittedBooking.businessName}."
                                )
                            },
                            icon = Icons.AutoMirrored.Filled.Chat,
                            useRedAccent = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextButton(onClick = onClearConfirmation) {
                            Text(
                                BilingualContent.t(language, "Create Another Booking", "మరో బుకింగ్ చేయండి"),
                                color = ElectricBlueBright
                            )
                        }
                    }
                }
            }
        }

        // Header with Official Logo
        item {
            AlphaLogoBannerHeader(
                eyebrow = BilingualContent.t(language, "BOOKING & DIGITAL AGREEMENT", "బుకింగ్ & డిజిటల్ ఒప్పందం"),
                title = BilingualContent.t(language, "Book Your AI Advertisement", "మీ AI ప్రకటన బుక్ చేయండి"),
                subtitle = BilingualContent.t(
                    language,
                    "Complete project details, review the 10-point agreement, and set separate publishing rights.",
                    "ప్రాజెక్ట్ వివరాలు, 10-అంశాల నిర్మాణ ఒప్పందం మరియు వేరు ప్రచురణ హక్కులను పూర్తి చేయండి."
                )
            )
        }

        if (!isSignedIn) {
            item {
                AlphaCard(borderColor = ElectricBlue.copy(alpha = 0.7f)) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = BilingualContent.t(
                                language,
                                "Guest Booking Mode — No Login Required",
                                "గెస్ట్ బుకింగ్ మోడ్ — లాగిన్ అవసరం లేదు"
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = ElectricBlueBright
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "You can submit your advertisement booking and download your Agreement PDF right now without signing in. Save your Reference Number or sign in later with Mobile OTP / Email.",
                                "మీరు లాగిన్ చేయకుండానే ప్రకటన బుకింగ్ సమర్పించి ఒప్పందం PDF డౌన్‌లోడ్ చేసుకోవచ్చు."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                        AlphaOutlineButton(
                            text = BilingualContent.t(language, "Optional: Sign In (Mobile / Email)", "ఐచ్ఛికం: లాగిన్ (మొబైల్ / ఇమెయిల్)"),
                            onClick = onOpenLogin,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // SECTION 1: BOOKING DETAILS
        item {
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = BilingualContent.t(language, "1. Advertisement Booking Details", "1. ప్రకటన బుకింగ్ వివరాలు"),
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricBlueBright
                    )
                    OutlinedTextField(
                        value = form.clientFullName,
                        onValueChange = { v -> onUpdateForm { it.copy(clientFullName = v) } },
                        label = { Text(BilingualContent.t(language, "Client Full Name *", "క్లయింట్ పూర్తి పేరు *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.businessName,
                        onValueChange = { v -> onUpdateForm { it.copy(businessName = v) } },
                        label = { Text(BilingualContent.t(language, "Business / Company Name *", "వ్యాపారం / సంస్థ పేరు *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.businessAddress,
                        onValueChange = { v -> onUpdateForm { it.copy(businessAddress = v) } },
                        label = { Text(BilingualContent.t(language, "Business Address *", "వ్యాపార చిరునామా *")) },
                        colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.whatsappNumber,
                        onValueChange = { v -> onUpdateForm { it.copy(whatsappNumber = v) } },
                        label = { Text(BilingualContent.t(language, "WhatsApp Number *", "వాట్సాప్ నంబర్ *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.emailAddress,
                        onValueChange = { v -> onUpdateForm { it.copy(emailAddress = v) } },
                        label = { Text(BilingualContent.t(language, "Email Address (Optional)", "ఇమెయిల్ (ఐచ్ఛికం)")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.adTitle,
                        onValueChange = { v -> onUpdateForm { it.copy(adTitle = v) } },
                        label = { Text(BilingualContent.t(language, "Advertisement Title *", "ప్రకటన శీర్షిక *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.productServiceName,
                        onValueChange = { v -> onUpdateForm { it.copy(productServiceName = v) } },
                        label = { Text(BilingualContent.t(language, "Product / Service Name *", "ఉత్పత్తి / సేవ పేరు *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )

                    Text(BilingualContent.t(language, "Advertisement Type *", "ప్రకటన రకం *"), style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        BilingualContent.AD_TYPES.forEach { type ->
                            val label = if (language == AppLanguage.TELUGU) BilingualContent.AD_TYPES_TE[type] ?: type else type
                            FilterChip(
                                selected = form.adType == type,
                                onClick = { onUpdateForm { it.copy(adType = type) } },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue,
                                    selectedLabelColor = IvoryWhite,
                                    containerColor = NavyElevated,
                                    labelColor = IvoryWhite
                                )
                            )
                        }
                    }

                    Text(BilingualContent.t(language, "Advertisement Language *", "ప్రకటన భాష *"), style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BilingualContent.AD_LANGUAGES.forEach { langOpt ->
                            val label = if (language == AppLanguage.TELUGU) BilingualContent.AD_LANGUAGES_TE[langOpt] ?: langOpt else langOpt
                            FilterChip(
                                selected = form.adLanguage == langOpt,
                                onClick = { onUpdateForm { it.copy(adLanguage = langOpt) } },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue,
                                    selectedLabelColor = IvoryWhite,
                                    containerColor = NavyElevated,
                                    labelColor = IvoryWhite
                                )
                            )
                        }
                    }

                    Text(BilingualContent.t(language, "Video Duration *", "వీడియో నిడివి *"), style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        BilingualContent.VIDEO_DURATIONS.forEach { dur ->
                            FilterChip(
                                selected = form.videoDuration == dur,
                                onClick = { onUpdateForm { it.copy(videoDuration = dur) } },
                                label = { Text(dur) },
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
                        value = form.deliverablesCount,
                        onValueChange = { v -> onUpdateForm { it.copy(deliverablesCount = v.filter { c -> c.isDigit() }) } },
                        label = { Text(BilingualContent.t(language, "Number of Images / Videos Required *", "కావలసిన చిత్రాలు / వీడియోల సంఖ్య *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )

                    Text(BilingualContent.t(language, "Budget Range *", "బడ్జెట్ పరిధి *"), style = MaterialTheme.typography.labelMedium, color = ElectricBlueBright)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        BilingualContent.BUDGET_RANGES.forEach { budget ->
                            FilterChip(
                                selected = form.budgetRange == budget,
                                onClick = { onUpdateForm { it.copy(budgetRange = budget) } },
                                label = { Text(budget) },
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
                        value = form.agreedPrice,
                        onValueChange = { v -> onUpdateForm { it.copy(agreedPrice = v) } },
                        label = { Text(BilingualContent.t(language, "Agreed Project Price (if discussed)", "అంగీకరించిన ప్రాజెక్ట్ ధర")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.advancePaymentDetails,
                        onValueChange = { v -> onUpdateForm { it.copy(advancePaymentDetails = v) } },
                        label = { Text(BilingualContent.t(language, "Advance Payment Details", "అడ్వాన్స్ చెల్లింపు వివరాలు")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.projectDescription,
                        onValueChange = { v -> onUpdateForm { it.copy(projectDescription = v) } },
                        label = { Text(BilingualContent.t(language, "Project / Advertisement Description *", "ప్రకటన పూర్తి వివరణ *")) },
                        minLines = 3, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.specialInstructions,
                        onValueChange = { v -> onUpdateForm { it.copy(specialInstructions = v) } },
                        label = { Text(BilingualContent.t(language, "Special Instructions", "ప్రత్యేక సూచనలు")) },
                        minLines = 2, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )

                    // Expected Delivery Date Picker
                    OutlinedTextField(
                        value = form.expectedDeliveryDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(BilingualContent.t(language, "Preferred / Expected Delivery Date *", "ఆశించిన డెలివరీ తేదీ *")) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick Date", tint = ElectricBlueBright)
                            }
                        },
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )

                    // Zero-Permission Android Photo Picker
                    AlphaOutlineButton(
                        text = BilingualContent.t(
                            language,
                            if (form.referenceMediaNames.isEmpty()) "Select Reference Images / Videos (Photo Picker)"
                            else "Selected ${form.referenceMediaNames.size} Reference File(s)",
                            if (form.referenceMediaNames.isEmpty()) "రిఫరెన్స్ చిత్రాలు / వీడియోలు ఎంచుకోండి"
                            else "${form.referenceMediaNames.size} ఫైల్స్ ఎంచుకోబడ్డాయి"
                        ),
                        onClick = {
                            mediaPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        },
                        icon = Icons.Filled.AttachFile,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (form.referenceMediaNames.isNotEmpty()) {
                        Text(
                            text = "Files: ${form.referenceMediaNames.joinToString(", ")}. Note: Full cloud media bucket upload requires Firebase Storage in the Firebase Console; filenames are recorded in your brief.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                    }
                }
            }
        }

        // SECTION 2: 10-POINT AI ADVERTISEMENT PRODUCTION AGREEMENT (with Official Logo Header)
        item {
            AlphaCard(borderColor = ElectricBlue.copy(alpha = 0.7f)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AlphaMediaLogo(
                            modifier = Modifier.width(82.dp),
                            cornerRadius = 10.dp,
                            horizontalPadding = 5.dp,
                            verticalPadding = 3.dp
                        )
                        Text(
                            text = BilingualContent.t(language, "2. AI Advertisement Production Agreement", "2. AI ప్రకటన నిర్మాణ ఒప్పందం"),
                            style = MaterialTheme.typography.titleMedium,
                            color = ElectricBlueBright,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BilingualContent.productionAgreementClauses.forEach { (enClause, teClause) ->
                        Text(
                            text = BilingualContent.t(language, enClause, teClause),
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateSilver
                        )
                    }
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f))
                    LabeledCheckbox(
                        checked = form.productionAgreementAccepted,
                        onCheckedChange = { checked -> onUpdateForm { it.copy(productionAgreementAccepted = checked) } },
                        label = BilingualContent.t(
                            language,
                            "ACCEPT AGREEMENT: I have read and accept all 10 AI Advertisement Production Agreement terms. *",
                            "ఒప్పందాన్ని ఆమోదిస్తున్నాను: పై 10 AI ప్రకటన నిర్మాణ నిబంధనలను నేను చదివి ఆమోదిస్తున్నాను. *"
                        )
                    )
                }
            }
        }

        // SECTION 3: SEPARATE PUBLISHING RIGHTS & DISCLOSURES
        item {
            AlphaCard(borderColor = CinematicRed.copy(alpha = 0.75f)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = CinematicRedBright)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = BilingualContent.t(
                                language,
                                "3. PUBLISHING PERMISSION & Rights (Optional & Separate)",
                                "3. ప్రచురణ అనుమతి & హక్కులు (ఐచ్ఛికం & వేరు)"
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryWhite
                        )
                    }
                    Text(
                        text = BilingualContent.t(
                            language,
                            "Publishing permission is strictly optional and separate from production. Silence or booking submission NEVER grants publishing consent.",
                            "ప్రచురణ అనుమతి పూర్తిగా ఐచ్ఛికం మరియు వేరుగా ఉంటుంది. బుకింగ్ సమర్పించడం వల్ల ప్రచురణ అనుమతి ఇవ్వబడినట్లు కాదు."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateSilver
                    )

                    Text(
                        BilingualContent.t(language, "A. Client Advertisement Publishing Permission", "A. క్లయింట్ ప్రకటన ప్రచురణ అనుమతి"),
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricBlueBright
                    )
                    LabeledCheckbox(
                        checked = form.publishClientSocial,
                        onCheckedChange = { checked ->
                            onUpdateForm {
                                it.copy(
                                    publishClientSocial = checked,
                                    publishDoNotPublish = if (checked) false else (!it.publishAgencySocial && !it.publishAgencyPortfolio)
                                )
                            }
                        },
                        label = BilingualContent.t(
                            language,
                            "I authorize ALPHA MEDIA to publish the approved advertisement on my business's social media accounts.",
                            "నా వ్యాపార సోషల్ మీడియా ఖాతాలలో ఆమోదించిన ప్రకటనను ప్రచురించడానికి ALPHA MEDIA కు అనుమతి ఇస్తున్నాను."
                        )
                    )
                    LabeledCheckbox(
                        checked = form.publishAgencySocial,
                        onCheckedChange = { checked ->
                            onUpdateForm {
                                it.copy(
                                    publishAgencySocial = checked,
                                    publishDoNotPublish = if (checked) false else (!it.publishClientSocial && !it.publishAgencyPortfolio)
                                )
                            }
                        },
                        label = BilingualContent.t(
                            language,
                            "I authorize ALPHA MEDIA to publish the approved advertisement on its official social media accounts.",
                            "ALPHA MEDIA అధికారిక సోషల్ మీడియా ఖాతాలలో ఆమోదించిన ప్రకటనను ప్రచురించడానికి అనుమతి ఇస్తున్నాను."
                        )
                    )
                    LabeledCheckbox(
                        checked = form.publishAgencyPortfolio,
                        onCheckedChange = { checked ->
                            onUpdateForm {
                                it.copy(
                                    publishAgencyPortfolio = checked,
                                    publishDoNotPublish = if (checked) false else (!it.publishClientSocial && !it.publishAgencySocial)
                                )
                            }
                        },
                        label = BilingualContent.t(
                            language,
                            "I authorize ALPHA MEDIA to use the approved advertisement in its portfolio and promotional demonstrations.",
                            "ALPHA MEDIA పోర్ట్‌ఫోలియో మరియు ప్రదర్శనలలో ఉపయోగించడానికి అనుమతి ఇస్తున్నాను."
                        )
                    )
                    LabeledCheckbox(
                        checked = form.publishDoNotPublish,
                        onCheckedChange = { checked ->
                            if (checked) {
                                onUpdateForm {
                                    it.copy(
                                        publishClientSocial = false,
                                        publishAgencySocial = false,
                                        publishAgencyPortfolio = false,
                                        publishDoNotPublish = true
                                    )
                                }
                            }
                        },
                        label = BilingualContent.t(
                            language,
                            "I do not authorize ALPHA MEDIA to publish my advertisement publicly.",
                            "నా ప్రకటనను బహిరంగంగా ప్రచురించడానికి ALPHA MEDIA కు అనుమతి ఇవ్వడం లేదు."
                        )
                    )

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f))
                    Text(
                        BilingualContent.t(language, "B. AI Content Disclosure *", "B. AI కంటెంట్ ప్రకటన *"),
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricBlueBright
                    )
                    LabeledCheckbox(
                        checked = form.aiContentDisclosureAccepted,
                        onCheckedChange = { c -> onUpdateForm { it.copy(aiContentDisclosureAccepted = c) } },
                        label = BilingualContent.t(
                            language,
                            "I understand that AI tools may be used for generating images, videos, voices, characters or other creative elements.",
                            "చిత్రాలు, వీడియోలు, వాయిస్‌లు లేదా పాత్రల రూపకల్పనలో AI సాధనాలు ఉపయోగించబడతాయని నేను అర్థం చేసుకున్నాను."
                        )
                    )
                    LabeledCheckbox(
                        checked = form.realPersonVoiceConsentAccepted,
                        onCheckedChange = { c -> onUpdateForm { it.copy(realPersonVoiceConsentAccepted = c) } },
                        label = BilingualContent.t(
                            language,
                            "I understand that any real person's face or voice must only be used with appropriate permission.",
                            "నిజమైన వ్యక్తి ముఖం లేదా గొంతు ఉపయోగించడానికి తగిన అనుమతి తప్పనిసరి అని నేను అర్థం చేసుకున్నాను."
                        )
                    )

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f))
                    Text(
                        BilingualContent.t(language, "C. Client Approval & Accuracy", "C. క్లయింట్ ఆమోదం & ఖచ్చితత్వం"),
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricBlueBright
                    )
                    LabeledCheckbox(
                        checked = form.clientApprovalChecked,
                        onCheckedChange = { c -> onUpdateForm { it.copy(clientApprovalChecked = c) } },
                        label = BilingualContent.t(
                            language,
                            "I confirm that the business details, product claims, images and contact information supplied are accurate. *",
                            "నేను అందించిన వ్యాపార వివరాలు, ఉత్పత్తి సమాచారం మరియు సంప్రదింపు వివరాలు సరైనవని ధృవీకరిస్తున్నాను. *"
                        )
                    )
                    LabeledCheckbox(
                        checked = form.clientFinalApproved,
                        onCheckedChange = { c ->
                            onUpdateForm { it.copy(clientFinalApproved = c, clientApprovalAccuracy = c) }
                        },
                        label = BilingualContent.t(
                            language,
                            "I have reviewed and approved the final advertisement (check only after preview review).",
                            "తుది ప్రకటనను పరిశీలించి ఆమోదించాను (ప్రివ్యూ చూసిన తర్వాత మాత్రమే ఎంచుకోండి)."
                        )
                    )
                }
            }
        }

        // SECTION 4: DIGITAL CONFIRMATION, PREVIEW, PDF & SUBMIT
        item {
            AlphaCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = BilingualContent.t(language, "4. Digital Agreement Confirmation", "4. డిజిటల్ ఒప్పంద నిర్ధారణ"),
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricBlueBright
                    )
                    Text(
                        text = "Booking Ref: ${form.referenceNumber}   •   Agreement Ref: ${form.agreementReferenceNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = CinematicRedBright
                    )
                    OutlinedTextField(
                        value = form.clientSignature,
                        onValueChange = { v -> onUpdateForm { it.copy(clientSignature = v) } },
                        label = { Text(BilingualContent.t(language, "Client Digital Signature (Typed Full Name) *", "క్లయింట్ డిజిటల్ సంతకం (పూర్తి పేరు) *")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.agreementDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(BilingualContent.t(language, "Date of Agreement", "ఒప్పంద తేదీ")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.representativeName,
                        onValueChange = { v -> onUpdateForm { it.copy(representativeName = v) } },
                        label = { Text(BilingualContent.t(language, "ALPHA MEDIA Representative Name", "ALPHA MEDIA ప్రతినిధి పేరు")) },
                        singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = BilingualContent.t(
                            language,
                            "Note: Formal legal e-signatures require an external e-signature service & appropriate legal review.",
                            "గమనిక: చట్టపరంగా కట్టుబడే ఇ-సిగ్నేచర్ల కోసం బాహ్య ఇ-సిగ్నేచర్ సేవ మరియు న్యాయ సమీక్ష అవసరం."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateSilver
                    )

                    if (form.validationError != null) {
                        Surface(
                            color = CrimsonContainer,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CrimsonError),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = form.validationError,
                                style = MaterialTheme.typography.bodySmall,
                                color = IvoryWhite,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AlphaOutlineButton(
                            text = BilingualContent.t(language, "VIEW AGREEMENT", "ఒప్పందం చూడండి"),
                            onClick = { showAgreementPreview = true },
                            icon = Icons.Filled.Description,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_agreement_button")
                        )
                        AlphaOutlineButton(
                            text = BilingualContent.t(language, "DOWNLOAD PDF", "PDF డౌన్‌లోడ్"),
                            onClick = {
                                val previewModel = formToPreviewBooking(form)
                                AgreementPdfGenerator.generateAgreementPdf(context, previewModel).fold(
                                    onSuccess = { uri -> openPdfUri(context, uri) },
                                    onFailure = { onShowMessage(it.localizedMessage ?: "PDF failed") }
                                )
                            },
                            icon = Icons.Filled.PictureAsPdf,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_draft_pdf_button")
                        )
                    }

                    AlphaPrimaryButton(
                        text = if (form.isSubmitting) {
                            BilingualContent.t(language, "Submitting...", "సమర్పిస్తోంది...")
                        } else {
                            BilingualContent.t(language, "SUBMIT BOOKING", "బుకింగ్ సమర్పించండి")
                        },
                        onClick = onSubmitBooking,
                        enabled = !form.isSubmitting,
                        icon = Icons.AutoMirrored.Filled.Send,
                        useRedAccent = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_booking_button")
                    )

                    if (form.isSubmitting) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = ElectricBlueBright, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }

        item {
            AlphaBrandFooter(language = language)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
                        onUpdateForm { it.copy(expectedDeliveryDate = formatted) }
                    }
                    showDatePicker = false
                }) { Text("OK", color = ElectricBlueBright) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = SlateSilver) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAgreementPreview) {
        AgreementFullPreviewDialog(
            language = language,
            form = form,
            onDismiss = { showAgreementPreview = false },
            onAcceptAgreement = {
                onUpdateForm {
                    it.copy(
                        productionAgreementAccepted = true,
                        aiContentDisclosureAccepted = true,
                        realPersonVoiceConsentAccepted = true,
                        clientApprovalChecked = true
                    )
                }
                showAgreementPreview = false
            }
        )
    }
}

@Composable
private fun LabeledCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ElectricBlue,
                uncheckedColor = SlateSilver,
                checkmarkColor = IvoryWhite
            )
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = IvoryWhite,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun AgreementFullPreviewDialog(
    language: AppLanguage,
    form: BookingFormState,
    onDismiss: () -> Unit,
    onAcceptAgreement: () -> Unit
) {
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
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        AlphaMediaLogo(
                            modifier = Modifier.width(68.dp),
                            cornerRadius = 8.dp,
                            horizontalPadding = 4.dp,
                            verticalPadding = 2.dp
                        )
                        Text(
                            text = BilingualContent.t(language, "Complete Agreement Preview", "పూర్తి ఒప్పంద ప్రివ్యూ"),
                            style = MaterialTheme.typography.titleLarge,
                            color = ElectricBlueBright
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = IvoryWhite)
                    }
                }
                Text(
                    "ALPHA MEDIA • CREATE • EDIT • INSPIRE\nBooking Ref: ${form.referenceNumber} | Agreement: ${form.agreementReferenceNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlueBright
                )
                Text(
                    "Client: ${form.clientFullName.ifBlank { "-" }} (${form.businessName.ifBlank { "-" }})\n" +
                        "Address: ${form.businessAddress.ifBlank { "-" }}\n" +
                        "WhatsApp: ${form.whatsappNumber.ifBlank { "-" }}\n" +
                        "Ad: ${form.adTitle.ifBlank { "-" }} | ${form.adType} | ${form.adLanguage} | ${form.videoDuration}\n" +
                        "Expected Delivery: ${form.expectedDeliveryDate.ifBlank { "-" }} | Budget: ${form.budgetRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IvoryWhite
                )
                HorizontalDivider(color = NavyBorder)
                BilingualContent.productionAgreementClauses.forEach { (en, te) ->
                    Text(BilingualContent.t(language, en, te), style = MaterialTheme.typography.bodySmall, color = SlateSilver)
                }
                HorizontalDivider(color = NavyBorder)
                Text(
                    "Publishing Permission: " + if (form.publishDoNotPublish) {
                        "NOT AUTHORIZED (Private)"
                    } else {
                        "ClientSocial=${form.publishClientSocial}, AgencySocial=${form.publishAgencySocial}, Portfolio=${form.publishAgencyPortfolio}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricBlueBright
                )
                Text(
                    "Signature: ${form.clientSignature.ifBlank { "(Unsigned)" }} | Date: ${form.agreementDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IvoryWhite
                )
                AlphaPrimaryButton(
                    text = BilingualContent.t(language, "ACCEPT AGREEMENT", "ఒప్పందాన్ని ఆమోదించండి"),
                    onClick = onAcceptAgreement,
                    useRedAccent = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun formToPreviewBooking(form: BookingFormState): BookingAgreement {
    val anyPub = !form.publishDoNotPublish &&
        (form.publishClientSocial || form.publishAgencySocial || form.publishAgencyPortfolio)
    return BookingAgreement(
        referenceNumber = form.referenceNumber,
        agreementReferenceNumber = form.agreementReferenceNumber,
        clientFullName = form.clientFullName.ifBlank { "Draft Client" },
        businessName = form.businessName.ifBlank { "Draft Business" },
        businessAddress = form.businessAddress.ifBlank { "Draft Address" },
        whatsappNumber = form.whatsappNumber.ifBlank { "-" },
        emailAddress = form.emailAddress,
        adTitle = form.adTitle.ifBlank { "Draft Ad" },
        productServiceName = form.productServiceName.ifBlank { "-" },
        adType = form.adType,
        adLanguage = form.adLanguage,
        videoDuration = form.videoDuration,
        deliverablesCount = form.deliverablesCount.toLongOrNull() ?: 1L,
        budgetRange = form.budgetRange,
        agreedPrice = form.agreedPrice,
        advancePaymentDetails = form.advancePaymentDetails,
        projectDescription = form.projectDescription.ifBlank { "Draft project description" },
        specialInstructions = form.specialInstructions,
        referenceMediaSummary = form.referenceMediaNames.joinToString(", "),
        expectedDeliveryDate = form.expectedDeliveryDate.ifBlank { "TBD" },
        productionAgreementAccepted = form.productionAgreementAccepted,
        aiContentDisclosureAccepted = form.aiContentDisclosureAccepted,
        realPersonVoiceConsentAccepted = form.realPersonVoiceConsentAccepted,
        clientApprovalChecked = form.clientApprovalChecked,
        clientFinalApproved = form.clientFinalApproved,
        clientApprovalAccuracy = form.clientApprovalAccuracy,
        publishClientSocial = form.publishClientSocial,
        publishAgencySocial = form.publishAgencySocial,
        publishAgencyPortfolio = form.publishAgencyPortfolio,
        publishDoNotPublish = !anyPub,
        clientSignature = form.clientSignature.ifBlank { "Pending Signature" },
        agreementDate = form.agreementDate,
        representativeName = form.representativeName,
        publishingStatus = if (anyPub) "AUTHORIZED" else "NOT_AUTHORIZED"
    )
}
