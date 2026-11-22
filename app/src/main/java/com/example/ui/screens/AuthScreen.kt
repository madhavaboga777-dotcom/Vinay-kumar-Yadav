package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.auth.AlphaAuthHelper
import com.example.auth.findActivity
import com.example.data.AlphaRepository
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.ui.components.AlphaBrandFooter
import com.example.ui.components.AlphaMediaLogo
import com.example.ui.components.AlphaOutlineButton
import com.example.ui.components.AlphaPrimaryButton
import com.example.ui.components.openWhatsAppEnquiry
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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onCheckAdminAccess: suspend () -> Boolean,
    onAuthSuccess: (preferredDashboardTab: Int) -> Unit,
    onBookWithoutLogin: () -> Unit,
    showTopLanguageToggle: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isTe = language == AppLanguage.TELUGU

    var selectedLoginTab by remember { mutableIntStateOf(0) } // 0=Mobile OTP, 1=Email (Optional), 2=Admin
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // Mobile OTP state
    var countryCode by remember { mutableStateOf("+91") }
    var mobileNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    // Email (Optional) state
    var isEmailRegisterMode by remember { mutableStateOf(false) }
    var emailDisplayName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientPassword by remember { mutableStateOf("") }
    var showClientPassword by remember { mutableStateOf(false) }

    // Admin Login state
    var adminEmail by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var showAdminPassword by remember { mutableStateOf(false) }
    var needsAdminVerification by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricBlue,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = IvoryWhite,
        unfocusedTextColor = IvoryWhite,
        focusedLabelColor = ElectricBlueBright,
        unfocusedLabelColor = SlateSilver
    )

    fun triggerPhoneOtp(isResend: Boolean = false) {
        errorMessage = null
        infoMessage = null
        val normalized = AlphaAuthHelper.normalizePhoneE164(countryCode, mobileNumber)
        if (normalized == null) {
            errorMessage = if (isTe) {
                "దయచేసి సరైన 10-అంకెల మొబైల్ నంబర్ నమోదు చేయండి."
            } else {
                "Please enter a valid 10-digit mobile number."
            }
            return
        }
        val activity = context.findActivity()
        if (activity == null) {
            errorMessage = if (isTe) {
                "మొబైల్ ధృవీకరణకు యాక్టివిటీ అందుబాటులో లేదు."
            } else {
                "Unable to access Activity for phone verification."
            }
            return
        }

        isLoading = true
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                scope.launch {
                    AlphaAuthHelper.signInWithPhoneCredential(credential).fold(
                        onSuccess = {
                            isLoading = false
                            onAuthSuccess(0)
                        },
                        onFailure = { err ->
                            isLoading = false
                            errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                        }
                    )
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false
                errorMessage = AlphaAuthHelper.formatAuthError(e, isTe)
            }

            override fun onCodeSent(
                verId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                isLoading = false
                verificationId = verId
                resendToken = token
                infoMessage = if (isTe) {
                    "$normalized నంబర్‌కు 6-అంకెల OTP పంపబడింది."
                } else {
                    "A 6-digit verification OTP has been sent to $normalized."
                }
            }
        }

        AlphaAuthHelper.startPhoneVerification(
            activity = activity,
            phoneE164 = normalized,
            callbacks = callbacks,
            resendToken = if (isResend) resendToken else null
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StudioBlack, NavyMidnight, NavyRoyal, StudioBlack)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.18f, size.height * 0.16f),
                    radius = size.minDimension * 0.65f
                ),
                radius = size.minDimension * 0.65f,
                center = Offset(size.width * 0.18f, size.height * 0.16f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CinematicRed.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.25f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.85f, size.height * 0.25f)
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideLayout = maxWidth >= 720.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showTopLanguageToggle) {
                    Row(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1040.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("auth_language_toggle")
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(onClick = onToggleLanguage),
                            color = NavySurface,
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = "Switch language",
                                    tint = ElectricBlueBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.ENGLISH) "తెలుగులో చూడండి" else "View in English",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IvoryWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                @Composable
                fun BrandShowcaseSection(modifier: Modifier = Modifier) {
                    Column(
                        modifier = modifier,
                        horizontalAlignment = if (isWideLayout) Alignment.Start else Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AlphaMediaLogo(
                            modifier = Modifier.widthIn(max = 260.dp).fillMaxWidth(0.74f),
                            cornerRadius = 20.dp,
                            horizontalPadding = 14.dp,
                            verticalPadding = 10.dp
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "ALPHA MEDIA Account & Studio Portal",
                                "ALPHA MEDIA ఖాతా & స్టూడియో పోర్టల్"
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = ElectricBlueBright,
                            fontWeight = FontWeight.Bold,
                            textAlign = if (isWideLayout) TextAlign.Start else TextAlign.Center
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "MAKE YOUR BUSINESS STAND OUT!",
                                "మీ వ్యాపారానికి డిజిటల్ గుర్తింపు తీసుకురండి!"
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            color = IvoryWhite,
                            fontWeight = FontWeight.Bold,
                            textAlign = if (isWideLayout) TextAlign.Start else TextAlign.Center
                        )
                        Text(
                            text = BilingualContent.t(
                                language,
                                "${BilingualContent.SLOGAN_EN} Sign in via Mobile Number or optional Email to track bookings, or book immediately without logging in.",
                                "${BilingualContent.SLOGAN_TE} బుకింగ్స్ చూడటానికి మొబైల్ నంబర్ లేదా ఇమెయిల్‌తో లాగిన్ అవ్వండి, లేదా లాగిన్ లేకుండానే వెంటనే ప్రకటన బుక్ చేయండి."
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateSilver,
                            textAlign = if (isWideLayout) TextAlign.Start else TextAlign.Center
                        )

                        // Quick Guest Booking & WhatsApp Card
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = BilingualContent.t(
                                        language,
                                        "No Login Required for New Bookings",
                                        "కొత్త బుకింగ్ కోసం లాగిన్ అవసరం లేదు"
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = ElectricBlueBright,
                                    fontWeight = FontWeight.Bold
                                )
                                AlphaPrimaryButton(
                                    text = BilingualContent.t(
                                        language,
                                        "BOOK AN ADVERTISEMENT WITHOUT LOGIN",
                                        "లాగిన్ లేకుండా ప్రకటన బుక్ చేయండి"
                                    ),
                                    onClick = onBookWithoutLogin,
                                    icon = Icons.Filled.Description,
                                    useRedAccent = true,
                                    modifier = Modifier.fillMaxWidth().testTag("book_without_login_button")
                                )
                                AlphaOutlineButton(
                                    text = BilingualContent.t(
                                        language,
                                        "Chat with ALPHA MEDIA on WhatsApp",
                                        "WhatsApp లో ALPHA MEDIA తో మాట్లాడండి"
                                    ),
                                    onClick = {
                                        openWhatsAppEnquiry(
                                            context,
                                            BilingualContent.t(
                                                language,
                                                "Hello ALPHA MEDIA! I would like help with login or booking an AI advertisement.",
                                                "నమస్కారం ALPHA MEDIA! నాకు లాగిన్ లేదా AI ప్రకటన బుకింగ్ గురించి సహాయం కావాలి."
                                            )
                                        )
                                    },
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    modifier = Modifier.fillMaxWidth().testTag("auth_whatsapp_button")
                                )
                            }
                        }
                    }
                }

                @Composable
                fun LoginFormCard(modifier: Modifier = Modifier) {
                    Surface(
                        modifier = modifier,
                        color = NavySurface,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(ElectricBlue.copy(alpha = 0.7f), CinematicRed.copy(alpha = 0.7f))
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            TabRow(
                                selectedTabIndex = selectedLoginTab,
                                containerColor = StudioBlack,
                                contentColor = ElectricBlueBright,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedLoginTab]),
                                        color = if (selectedLoginTab == 2) CinematicRed else ElectricBlueBright
                                    )
                                }
                            ) {
                                Tab(
                                    selected = selectedLoginTab == 0,
                                    onClick = {
                                        selectedLoginTab = 0
                                        errorMessage = null
                                        infoMessage = null
                                    },
                                    modifier = Modifier.testTag("tab_mobile_login"),
                                    icon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    text = {
                                        Text(
                                            BilingualContent.t(language, "Mobile", "మొబైల్"),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                )
                                Tab(
                                    selected = selectedLoginTab == 1,
                                    onClick = {
                                        selectedLoginTab = 1
                                        errorMessage = null
                                        infoMessage = null
                                    },
                                    modifier = Modifier.testTag("tab_email_login"),
                                    icon = { Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    text = {
                                        Text(
                                            BilingualContent.t(language, "Email", "ఇమెయిల్"),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                )
                                Tab(
                                    selected = selectedLoginTab == 2,
                                    onClick = {
                                        selectedLoginTab = 2
                                        errorMessage = null
                                        infoMessage = null
                                    },
                                    modifier = Modifier.testTag("tab_admin_login"),
                                    icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    text = {
                                        Text(
                                            BilingualContent.t(language, "Admin", "అడ్మిన్"),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                )
                            }

                            AnimatedVisibility(visible = errorMessage != null) {
                                Surface(
                                    color = CrimsonContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, CrimsonError),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = errorMessage.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IvoryWhite,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = infoMessage != null) {
                                Surface(
                                    color = EmeraldContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, EmeraldSuccess),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = infoMessage.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = IvoryWhite
                                        )
                                    }
                                }
                            }

                            when (selectedLoginTab) {
                                // TAB 0: MOBILE NUMBER + OTP LOGIN
                                0 -> {
                                    Text(
                                        text = BilingualContent.t(
                                            language,
                                            "Sign in with your Mobile Number (OTP Verification)",
                                            "మీ మొబైల్ నంబర్ (OTP) తో సురక్షితంగా లాగిన్ అవ్వండి"
                                        ),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = IvoryWhite,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = countryCode,
                                            onValueChange = { if (it.length <= 5) countryCode = it },
                                            label = { Text(BilingualContent.t(language, "Code", "కోడ్")) },
                                            singleLine = true,
                                            enabled = verificationId == null && !isLoading,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            colors = fieldColors,
                                            modifier = Modifier.width(88.dp)
                                        )
                                        OutlinedTextField(
                                            value = mobileNumber,
                                            onValueChange = { mobileNumber = it },
                                            label = { Text(BilingualContent.t(language, "Mobile Number *", "మొబైల్ నంబర్ *")) },
                                            placeholder = { Text("98765 43210", color = SlateSilver.copy(alpha = 0.6f)) },
                                            singleLine = true,
                                            enabled = verificationId == null && !isLoading,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            colors = fieldColors,
                                            modifier = Modifier.weight(1f).testTag("mobile_number_input")
                                        )
                                    }

                                    if (verificationId == null) {
                                        Button(
                                            onClick = { triggerPhoneOtp(isResend = false) },
                                            enabled = !isLoading,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .testTag("send_otp_button"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ElectricBlue,
                                                contentColor = IvoryWhite
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = IvoryWhite, strokeWidth = 2.dp)
                                                Spacer(Modifier.width(8.dp))
                                            }
                                            Text(
                                                text = BilingualContent.t(language, "Send Verification OTP", "OTP పంపండి"),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        OutlinedTextField(
                                            value = otpCode,
                                            onValueChange = { if (it.length <= 6) otpCode = it },
                                            label = { Text(BilingualContent.t(language, "Enter 6-Digit OTP *", "6-అంకెల OTP నమోదు చేయండి *")) },
                                            singleLine = true,
                                            enabled = !isLoading,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = fieldColors,
                                            modifier = Modifier.fillMaxWidth().testTag("otp_code_input")
                                        )
                                        Button(
                                            onClick = {
                                                val vId = verificationId ?: return@Button
                                                errorMessage = null
                                                isLoading = true
                                                scope.launch {
                                                    AlphaAuthHelper.verifyOtpCode(vId, otpCode).fold(
                                                        onSuccess = {
                                                            isLoading = false
                                                            onAuthSuccess(0)
                                                        },
                                                        onFailure = { err ->
                                                            isLoading = false
                                                            errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                        }
                                                    )
                                                }
                                            },
                                            enabled = !isLoading,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .testTag("verify_otp_button"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ElectricBlue,
                                                contentColor = IvoryWhite
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = IvoryWhite, strokeWidth = 2.dp)
                                                Spacer(Modifier.width(8.dp))
                                            }
                                            Text(
                                                text = BilingualContent.t(language, "Verify OTP & Sign In", "OTP నిర్ధారించి లాగిన్ అవ్వండి"),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    verificationId = null
                                                    otpCode = ""
                                                    infoMessage = null
                                                    errorMessage = null
                                                },
                                                enabled = !isLoading
                                            ) {
                                                Text(
                                                    BilingualContent.t(language, "Change Number", "నంబర్ మార్చండి"),
                                                    color = SlateSilver
                                                )
                                            }
                                            TextButton(
                                                onClick = { triggerPhoneOtp(isResend = true) },
                                                enabled = !isLoading && resendToken != null
                                            ) {
                                                Text(
                                                    BilingualContent.t(language, "Resend OTP", "మళ్లీ OTP పంపండి"),
                                                    color = ElectricBlueBright
                                                )
                                            }
                                        }
                                    }
                                }

                                // TAB 1: EMAIL & PASSWORD LOGIN (OPTIONAL)
                                1 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = BilingualContent.t(
                                                language,
                                                "Email & Password (Optional)",
                                                "ఇమెయిల్ & పాస్‌వర్డ్ (ఐచ్ఛికం)"
                                            ),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = IvoryWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TextButton(
                                            onClick = {
                                                isEmailRegisterMode = !isEmailRegisterMode
                                                errorMessage = null
                                                infoMessage = null
                                            }
                                        ) {
                                            Text(
                                                text = if (isEmailRegisterMode) {
                                                    BilingualContent.t(language, "Already registered? Sign In", "ఖాతా ఉందా? లాగిన్")
                                                } else {
                                                    BilingualContent.t(language, "New Client? Register", "కొత్త ఖాతా నమోదు")
                                                },
                                                color = ElectricBlueBright,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }

                                    if (isEmailRegisterMode) {
                                        OutlinedTextField(
                                            value = emailDisplayName,
                                            onValueChange = { emailDisplayName = it },
                                            label = { Text(BilingualContent.t(language, "Full Name / Business Name", "పూర్తి పేరు / వ్యాపారం పేరు")) },
                                            singleLine = true,
                                            colors = fieldColors,
                                            modifier = Modifier.fillMaxWidth().testTag("email_name_input")
                                        )
                                    }

                                    OutlinedTextField(
                                        value = clientEmail,
                                        onValueChange = { clientEmail = it },
                                        label = { Text(BilingualContent.t(language, "Email Address *", "ఇమెయిల్ చిరునామా *")) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        colors = fieldColors,
                                        modifier = Modifier.fillMaxWidth().testTag("email_login_input")
                                    )

                                    OutlinedTextField(
                                        value = clientPassword,
                                        onValueChange = { clientPassword = it },
                                        label = { Text(BilingualContent.t(language, "Password *", "పాస్‌వర్డ్ *")) },
                                        singleLine = true,
                                        visualTransformation = if (showClientPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        trailingIcon = {
                                            IconButton(onClick = { showClientPassword = !showClientPassword }) {
                                                Icon(
                                                    imageVector = if (showClientPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = SlateSilver
                                                )
                                            }
                                        },
                                        colors = fieldColors,
                                        modifier = Modifier.fillMaxWidth().testTag("email_password_input")
                                    )

                                    Button(
                                        onClick = {
                                            errorMessage = null
                                            infoMessage = null
                                            val cleanEmail = clientEmail.trim()
                                            if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
                                                errorMessage = BilingualContent.t(language, "Please enter a valid email address.", "దయచేసి సరైన ఇమెయిల్ చిరునామా నమోదు చేయండి.")
                                                return@Button
                                            }
                                            if (clientPassword.length < 6) {
                                                errorMessage = BilingualContent.t(language, "Password must be at least 6 characters.", "పాస్‌వర్డ్ కనీసం 6 అక్షరాలు ఉండాలి.")
                                                return@Button
                                            }
                                            isLoading = true
                                            scope.launch {
                                                val result = if (isEmailRegisterMode) {
                                                    AlphaAuthHelper.registerWithEmail(emailDisplayName, cleanEmail, clientPassword)
                                                } else {
                                                    AlphaAuthHelper.signInWithEmail(cleanEmail, clientPassword)
                                                }
                                                result.fold(
                                                    onSuccess = {
                                                        isLoading = false
                                                        onAuthSuccess(0)
                                                    },
                                                    onFailure = { err ->
                                                        isLoading = false
                                                        errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                    }
                                                )
                                            }
                                        },
                                        enabled = !isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("email_submit_button"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricBlue,
                                            contentColor = IvoryWhite
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = IvoryWhite, strokeWidth = 2.dp)
                                            Spacer(Modifier.width(8.dp))
                                        }
                                        Text(
                                            text = if (isEmailRegisterMode) {
                                                BilingualContent.t(language, "Create Client Account", "క్లయింట్ ఖాతా సృష్టించండి")
                                            } else {
                                                BilingualContent.t(language, "Sign In with Email", "ఇమెయిల్‌తో సైన్ ఇన్")
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (!isEmailRegisterMode) {
                                        TextButton(
                                            onClick = {
                                                val cleanEmail = clientEmail.trim()
                                                if (!cleanEmail.contains("@")) {
                                                    errorMessage = BilingualContent.t(language, "Enter your email above to receive a password reset link.", "పాస్‌వర్డ్ రీసెట్ కోసం పైన మీ ఇమెయిల్ నమోదు చేయండి.")
                                                    return@TextButton
                                                }
                                                scope.launch {
                                                    AlphaAuthHelper.sendPasswordReset(cleanEmail).fold(
                                                        onSuccess = {
                                                            infoMessage = BilingualContent.t(
                                                                language,
                                                                "Password reset link sent to $cleanEmail.",
                                                                "$cleanEmail కు పాస్‌వర్డ్ రీసెట్ లింక్ పంపబడింది."
                                                            )
                                                        },
                                                        onFailure = { err ->
                                                            errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                        }
                                                    )
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text(
                                                BilingualContent.t(language, "Forgot Password?", "పాస్‌వర్డ్ మర్చిపోయారా?"),
                                                color = SlateSilver,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }

                                // TAB 2: ADMIN LOGIN WITH SECURE CREDENTIALS
                                2 -> {
                                    Surface(
                                        color = CrimsonContainer,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, CinematicRed.copy(alpha = 0.8f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.Lock, contentDescription = null, tint = CinematicRedBright, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = BilingualContent.t(
                                                    language,
                                                    "Authorized ALPHA MEDIA Studio Administrators Only.",
                                                    "అధికారిక ALPHA MEDIA స్టూడియో అడ్మినిస్ట్రేటర్లకు మాత్రమే."
                                                ),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = IvoryWhite,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = adminEmail,
                                        onValueChange = { adminEmail = it },
                                        label = { Text(BilingualContent.t(language, "Admin Email *", "అడ్మిన్ ఇమెయిల్ *")) },
                                        placeholder = { Text(AlphaRepository.BOOTSTRAP_ADMIN_EMAIL, color = SlateSilver.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        colors = fieldColors,
                                        modifier = Modifier.fillMaxWidth().testTag("admin_email_input")
                                    )

                                    OutlinedTextField(
                                        value = adminPassword,
                                        onValueChange = { adminPassword = it },
                                        label = { Text(BilingualContent.t(language, "Admin Password *", "అడ్మిన్ పాస్‌వర్డ్ *")) },
                                        singleLine = true,
                                        visualTransformation = if (showAdminPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        trailingIcon = {
                                            IconButton(onClick = { showAdminPassword = !showAdminPassword }) {
                                                Icon(
                                                    imageVector = if (showAdminPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                                    contentDescription = "Toggle admin password visibility",
                                                    tint = SlateSilver
                                                )
                                            }
                                        },
                                        colors = fieldColors,
                                        modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
                                    )

                                    Button(
                                        onClick = {
                                            errorMessage = null
                                            infoMessage = null
                                            needsAdminVerification = false
                                            val cleanEmail = adminEmail.trim()
                                            if (!cleanEmail.contains("@") || adminPassword.length < 6) {
                                                errorMessage = BilingualContent.t(
                                                    language,
                                                    "Enter valid admin email and password (min 6 characters).",
                                                    "సరైన అడ్మిన్ ఇమెయిల్ మరియు పాస్‌వర్డ్ (కనీసం 6 అక్షరాలు) నమోదు చేయండి."
                                                )
                                                return@Button
                                            }
                                            isLoading = true
                                            scope.launch {
                                                AlphaAuthHelper.signInWithEmail(cleanEmail, adminPassword).fold(
                                                    onSuccess = { user ->
                                                        val adminConfirmed = onCheckAdminAccess()
                                                        isLoading = false
                                                        if (adminConfirmed) {
                                                            onAuthSuccess(1)
                                                        } else {
                                                            needsAdminVerification = !user.isEmailVerified
                                                            errorMessage = if (!user.isEmailVerified) {
                                                                BilingualContent.t(
                                                                    language,
                                                                    "Signed in as ${user.email}, but your email must be verified before Admin Dashboard access is granted.",
                                                                    "${user.email} గా లాగిన్ అయ్యారు, కానీ అడ్మిన్ యాక్సెస్ కోసం ఇమెయిల్ ధృవీకరణ అవసరం."
                                                                )
                                                            } else {
                                                                BilingualContent.t(
                                                                    language,
                                                                    "This account is not registered as an authorized ALPHA MEDIA Administrator.",
                                                                    "ఈ ఖాతాకు ALPHA MEDIA అడ్మిన్ అనుమతి లేదు."
                                                                )
                                                            }
                                                        }
                                                    },
                                                    onFailure = { err ->
                                                        isLoading = false
                                                        errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                    }
                                                )
                                            }
                                        },
                                        enabled = !isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("admin_login_button"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CinematicRed,
                                            contentColor = IvoryWhite
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = IvoryWhite, strokeWidth = 2.dp)
                                            Spacer(Modifier.width(8.dp))
                                        }
                                        Text(
                                            text = BilingualContent.t(language, "Sign In to Admin Studio", "అడ్మిన్ స్టూడియోలో లాగిన్ అవ్వండి"),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (needsAdminVerification) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        AlphaAuthHelper.sendVerificationEmailToCurrentUser().fold(
                                                            onSuccess = {
                                                                infoMessage = BilingualContent.t(
                                                                    language,
                                                                    "Verification email sent. Check your inbox then tap Refresh Status.",
                                                                    "ధృవీకరణ ఇమెయిల్ పంపబడింది. మీ ఇన్‌బాక్స్ తనిఖీ చేసి Refresh పై నొక్కండి."
                                                                )
                                                            },
                                                            onFailure = { err ->
                                                                errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                            }
                                                        )
                                                    }
                                                },
                                                border = BorderStroke(1.dp, ElectricBlueBright),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(BilingualContent.t(language, "Send Verify Email", "ఇమెయిల్ పంపండి"), color = IvoryWhite)
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        AlphaAuthHelper.reloadCurrentUser()
                                                        if (onCheckAdminAccess()) {
                                                            onAuthSuccess(1)
                                                        } else {
                                                            errorMessage = BilingualContent.t(
                                                                language,
                                                                "Still awaiting email verification or /admins entry.",
                                                                "ఇమెయిల్ ధృవీకరణ ఇంకా పూర్తికాలేదు."
                                                            )
                                                        }
                                                    }
                                                },
                                                border = BorderStroke(1.dp, EmeraldSuccess),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Filled.Refresh, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text(BilingualContent.t(language, "Refresh", "రిఫ్రెష్"), color = IvoryWhite)
                                            }
                                        }
                                    }

                                    TextButton(
                                        onClick = {
                                            val cleanEmail = adminEmail.trim().ifBlank { AlphaRepository.BOOTSTRAP_ADMIN_EMAIL }
                                            scope.launch {
                                                AlphaAuthHelper.sendPasswordReset(cleanEmail).fold(
                                                    onSuccess = {
                                                        infoMessage = BilingualContent.t(
                                                            language,
                                                            "Admin password reset link sent to $cleanEmail.",
                                                            "$cleanEmail కు అడ్మిన్ పాస్‌వర్డ్ రీసెట్ లింక్ పంపబడింది."
                                                        )
                                                    },
                                                    onFailure = { err ->
                                                        errorMessage = AlphaAuthHelper.formatAuthError(err, isTe)
                                                    }
                                                )
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text(
                                            BilingualContent.t(language, "Reset Admin Password", "అడ్మిన్ పాస్‌వర్డ్ రీసెట్"),
                                            color = SlateSilver,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = ElectricBlueBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = BilingualContent.t(
                                        language,
                                        "Private Firestore storage • Client agreements stay confidential",
                                        "సురక్షితమైన Firestore నిల్వ • క్లయింట్ వివరాలు గోప్యంగా ఉంటాయి"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateSilver
                                )
                            }
                        }
                    }
                }

                if (isWideLayout) {
                    Row(
                        modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        BrandShowcaseSection(modifier = Modifier.weight(0.9f))
                        LoginFormCard(modifier = Modifier.weight(1.1f))
                    }
                } else {
                    Column(
                        modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BrandShowcaseSection(modifier = Modifier.fillMaxWidth())
                        LoginFormCard(modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = CinematicRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = BilingualContent.t(
                            language,
                            BilingualContent.STUDIO_LOCATION_EN,
                            BilingualContent.STUDIO_LOCATION_TE
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = IvoryWhite
                    )
                }

                Spacer(Modifier.height(18.dp))
                Box(modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth()) {
                    AlphaBrandFooter(language = language)
                }
            }
        }
    }
}
