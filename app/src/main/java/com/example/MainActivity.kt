package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.AlphaAuthHelper
import com.example.data.AlphaAiRepository
import com.example.data.AlphaRepository
import com.example.data.BilingualContent
import com.example.data.configureAppCheck
import com.example.model.AppLanguage
import com.example.ui.components.AlphaMediaLogo
import com.example.ui.components.AlphaTopBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingAgreementScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PortfolioScreen
import com.example.ui.screens.ServicesAndAiScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudioAdminScreen
import com.example.ui.theme.AlphaMediaTheme
import com.example.ui.theme.CinematicRedBright
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyMidnight
import com.example.ui.theme.NavyRoyal
import com.example.ui.theme.SlateSilver
import com.example.ui.theme.StudioBlack
import com.example.viewmodel.AlphaViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

object AlphaRoutes {
    const val HOME = "home"
    const val SERVICES = "services"
    const val PORTFOLIO = "portfolio"
    const val BOOK_AD = "book_ad"
    const val STUDIO = "studio"
    const val LOGIN = "login"
}

private data class NavDestinationItem(
    val route: String,
    val labelEn: String,
    val labelTe: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val ALPHA_NAV_ITEMS = listOf(
    NavDestinationItem(AlphaRoutes.HOME, "Home", "హోమ్", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    NavDestinationItem(AlphaRoutes.SERVICES, "Services & AI", "సేవలు & AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_services"),
    NavDestinationItem(AlphaRoutes.PORTFOLIO, "Portfolio", "పోర్ట్‌ఫోలియో", Icons.Filled.Collections, Icons.Outlined.Collections, "nav_portfolio"),
    NavDestinationItem(AlphaRoutes.BOOK_AD, "Book & Agree", "బుక్ & ఒప్పందం", Icons.Filled.Description, Icons.Outlined.Description, "nav_book"),
    NavDestinationItem(AlphaRoutes.STUDIO, "Dashboards", "డ్యాష్‌బోర్డ్స్", Icons.Filled.FolderShared, Icons.Outlined.FolderShared, "nav_studio")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            AlphaMediaTheme(darkTheme = true) {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val scope = rememberCoroutineScope()

    var showSplash by remember { mutableStateOf(true) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    Crossfade(
        targetState = if (showSplash) "splash" else "studio_${currentUser?.uid ?: "guest"}",
        animationSpec = tween(durationMillis = 420),
        label = "rootCrossfade"
    ) { screenState ->
        if (screenState == "splash") {
            SplashScreen(onFinished = { showSplash = false })
        } else {
            val user = currentUser
            val sessionKey = user?.uid ?: "guest"
            val userEmail = user?.email.orEmpty()
            val userPhone = user?.phoneNumber.orEmpty()
            val userDisplayName = user?.displayName.orEmpty()
            val userIdentifier = userPhone.ifBlank { userEmail }

            key(sessionKey) {
                AlphaStudioApp(
                    isSignedIn = user != null,
                    currentUserId = user?.uid.orEmpty(),
                    currentUserEmail = userEmail,
                    currentUserPhone = userPhone,
                    currentUserDisplayName = userDisplayName,
                    currentUserIdentifier = userIdentifier,
                    onSignOut = {
                        AlphaAuthHelper.signOutUser(
                            scope = scope,
                            onSignOutComplete = { currentUser = null }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AlphaStudioApp(
    isSignedIn: Boolean,
    currentUserId: String,
    currentUserEmail: String,
    currentUserPhone: String,
    currentUserDisplayName: String,
    currentUserIdentifier: String,
    onSignOut: () -> Unit
) {
    val viewModelKey = currentUserId.ifBlank { "guest" }
    val viewModel: AlphaViewModel = viewModel(
        key = viewModelKey,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                AlphaViewModel(
                    repository = AlphaRepository(db),
                    aiRepository = AlphaAiRepository(),
                    currentUserId = currentUserId,
                    currentUserEmail = currentUserEmail,
                    currentUserPhone = currentUserPhone,
                    currentUserDisplayName = currentUserDisplayName
                )
            }
        }
    )

    val language by viewModel.language.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val lastSubmittedBooking by viewModel.lastSubmittedBooking.collectAsStateWithLifecycle()
    val bookingForm by viewModel.bookingForm.collectAsStateWithLifecycle()
    val myBookingsState by viewModel.myBookingsState.collectAsStateWithLifecycle()
    val adminBookingsState by viewModel.adminBookingsState.collectAsStateWithLifecycle()
    val enquiriesState by viewModel.adminEnquiriesState.collectAsStateWithLifecycle()
    val portfolioState by viewModel.portfolioState.collectAsStateWithLifecycle()
    val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
    val historyState by viewModel.selectedBookingHistoryState.collectAsStateWithLifecycle()
    val aiResearchState by viewModel.aiResearchState.collectAsStateWithLifecycle()
    val aiStrategyState by viewModel.aiStrategyState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AlphaRoutes.HOME
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    val homeListState = rememberLazyListState()
    var preferredStudioTab by remember { mutableIntStateOf(0) }

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(AlphaRoutes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    AlphaMediaTheme(darkTheme = true, isTelugu = language == AppLanguage.TELUGU) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AlphaNavigationDrawerSheet(
                    language = language,
                    isSignedIn = isSignedIn,
                    isAdmin = isAdmin,
                    currentRoute = currentRoute,
                    preferredStudioTab = preferredStudioTab,
                    userIdentifier = currentUserIdentifier,
                    onNavigate = { route, studioTab ->
                        preferredStudioTab = studioTab
                        drawerScope.launch { drawerState.close() }
                        navigateTo(route)
                    },
                    onJumpToHomeSection = { sectionIndex ->
                        drawerScope.launch {
                            drawerState.close()
                            navigateTo(AlphaRoutes.HOME)
                            homeListState.animateScrollToItem(sectionIndex)
                        }
                    },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onSignOut = {
                        drawerScope.launch { drawerState.close() }
                        onSignOut()
                    }
                )
            }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useRail = maxWidth >= 600.dp

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NavyRoyal,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AlphaTopBar(
                            language = language,
                            isSignedIn = isSignedIn,
                            isAdmin = isAdmin,
                            userIdentifier = currentUserIdentifier,
                            showMenuButton = true,
                            onOpenDrawer = { drawerScope.launch { drawerState.open() } },
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onOpenLogin = { navigateTo(AlphaRoutes.LOGIN) },
                            onOpenAdminOrBookings = {
                                preferredStudioTab = if (isAdmin) 1 else 0
                                navigateTo(AlphaRoutes.STUDIO)
                            },
                            onSignOut = onSignOut
                        )
                    },
                    bottomBar = {
                        if (!useRail) {
                            NavigationBar(
                                containerColor = StudioBlack,
                                contentColor = IvoryWhite,
                                modifier = Modifier.navigationBarsPadding()
                            ) {
                                ALPHA_NAV_ITEMS.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateTo(item.route) },
                                        modifier = Modifier.testTag(item.testTag),
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.labelEn
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = if (item.route == AlphaRoutes.STUDIO && isAdmin) {
                                                    BilingualContent.t(language, "Client/Admin", "అడ్మిన్")
                                                } else {
                                                    BilingualContent.t(language, item.labelEn, item.labelTe)
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = IvoryWhite,
                                            selectedTextColor = ElectricBlueBright,
                                            indicatorColor = ElectricBlue,
                                            unselectedIconColor = SlateSilver,
                                            unselectedTextColor = SlateSilver
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (useRail) {
                            NavigationRail(
                                containerColor = StudioBlack,
                                contentColor = IvoryWhite
                            ) {
                                ALPHA_NAV_ITEMS.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { navigateTo(item.route) },
                                        modifier = Modifier.testTag(item.testTag),
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.labelEn
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = BilingualContent.t(language, item.labelEn, item.labelTe),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = NavigationRailItemDefaults.colors(
                                            selectedIconColor = IvoryWhite,
                                            selectedTextColor = ElectricBlueBright,
                                            indicatorColor = ElectricBlue,
                                            unselectedIconColor = SlateSilver,
                                            unselectedTextColor = SlateSilver
                                        )
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(NavyRoyal)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = AlphaRoutes.HOME
                            ) {
                                composable(AlphaRoutes.HOME) {
                                    HomeScreen(
                                        language = language,
                                        userDisplayName = currentUserDisplayName,
                                        userEmail = currentUserEmail,
                                        listState = homeListState,
                                        onExploreServices = { navigateTo(AlphaRoutes.SERVICES) },
                                        onViewPortfolio = { navigateTo(AlphaRoutes.PORTFOLIO) },
                                        onBookAd = { navigateTo(AlphaRoutes.BOOK_AD) },
                                        onSubmitEnquiry = { n, b, w, e, m ->
                                            viewModel.submitBusinessEnquiry(n, b, w, e, m)
                                        }
                                    )
                                }
                                composable(AlphaRoutes.SERVICES) {
                                    BackHandler { navigateTo(AlphaRoutes.HOME) }
                                    ServicesAndAiScreen(
                                        language = language,
                                        servicesState = servicesState,
                                        aiResearchState = aiResearchState,
                                        aiStrategyState = aiStrategyState,
                                        onBookService = { srv ->
                                            viewModel.prefillBookingFromService(srv)
                                            navigateTo(AlphaRoutes.BOOK_AD)
                                        },
                                        onRunMarketResearch = { biz, reg, goal ->
                                            viewModel.runSearchGroundedTrendResearch(biz, reg, goal)
                                        },
                                        onRunDeepStrategy = { biz, prod, type, lang, dur, notes ->
                                            viewModel.generateDeepCreativeStrategy(biz, prod, type, lang, dur, notes)
                                        },
                                        onUseStrategyInBooking = { biz, prod, type, lang, dur, brief ->
                                            viewModel.prefillBookingFromAiStrategy(biz, prod, type, lang, dur, brief)
                                            navigateTo(AlphaRoutes.BOOK_AD)
                                        }
                                    )
                                }
                                composable(AlphaRoutes.PORTFOLIO) {
                                    BackHandler { navigateTo(AlphaRoutes.HOME) }
                                    PortfolioScreen(
                                        language = language,
                                        portfolioState = portfolioState,
                                        onBookSimilar = { item ->
                                            viewModel.prefillBookingFromPortfolio(item)
                                            navigateTo(AlphaRoutes.BOOK_AD)
                                        }
                                    )
                                }
                                composable(AlphaRoutes.BOOK_AD) {
                                    BackHandler { navigateTo(AlphaRoutes.HOME) }
                                    BookingAgreementScreen(
                                        language = language,
                                        form = bookingForm,
                                        lastSubmittedBooking = lastSubmittedBooking,
                                        isSignedIn = isSignedIn,
                                        onOpenLogin = { navigateTo(AlphaRoutes.LOGIN) },
                                        onUpdateForm = { viewModel.updateBookingForm(it) },
                                        onSubmitBooking = { viewModel.submitBookingAndAgreement() },
                                        onClearConfirmation = { viewModel.clearLastSubmittedBooking() },
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                                composable(AlphaRoutes.STUDIO) {
                                    BackHandler { navigateTo(AlphaRoutes.HOME) }
                                    StudioAdminScreen(
                                        language = language,
                                        isSignedIn = isSignedIn,
                                        isAdmin = isAdmin,
                                        preferredTab = preferredStudioTab,
                                        myBookingsState = myBookingsState,
                                        adminBookingsState = adminBookingsState,
                                        enquiriesState = enquiriesState,
                                        portfolioState = portfolioState,
                                        servicesState = servicesState,
                                        historyState = historyState,
                                        onOpenLogin = { navigateTo(AlphaRoutes.LOGIN) },
                                        onBookWithoutLogin = { navigateTo(AlphaRoutes.BOOK_AD) },
                                        onSelectBookingForHistory = { viewModel.selectBookingForHistory(it) },
                                        onUpdatePublishingRights = { b, c, a, p, d ->
                                            viewModel.updatePublishingRights(b, c, a, p, d)
                                        },
                                        onSubmitClientApproval = { b, app, acc, rev, inst ->
                                            viewModel.submitClientRevisionOrApproval(b, app, acc, rev, inst)
                                        },
                                        onAdminUpdateBooking = { b, ps, pub, pay, pr, adv, rep, rev, del ->
                                            viewModel.adminUpdateBooking(b, ps, pub, pay, pr, adv, rep, rev, del)
                                        },
                                        onAdminMarkEnquiryResponded = { viewModel.adminMarkEnquiryResponded(it) },
                                        onAdminAddPortfolioItem = { tEn, tTe, cat, dEn, dTe, dur, vk, sb ->
                                            viewModel.adminAddPortfolioItem(tEn, tTe, cat, dEn, dTe, dur, vk, sb)
                                        },
                                        onAdminDeletePortfolioItem = { viewModel.adminDeletePortfolioItem(it) },
                                        onAdminAddService = { idx, tEn, tTe, dEn, dTe, dlEn, dlTe, adKey ->
                                            viewModel.adminAddService(idx, tEn, tTe, dEn, dTe, dlEn, dlTe, adKey)
                                        },
                                        onAdminDeleteService = { viewModel.adminDeleteService(it) },
                                        onShowMessage = { viewModel.showMessage(it) }
                                    )
                                }
                                composable(AlphaRoutes.LOGIN) {
                                    BackHandler { navigateTo(AlphaRoutes.HOME) }
                                    val repo = remember(context) { AlphaRepository(context) }
                                    AuthScreen(
                                        language = language,
                                        onToggleLanguage = { viewModel.toggleLanguage() },
                                        onCheckAdminAccess = {
                                            val adminOk = repo.checkIsAdmin()
                                            if (adminOk) {
                                                viewModel.refreshAdminStatus()
                                            }
                                            adminOk
                                        },
                                        onAuthSuccess = { targetTab ->
                                            preferredStudioTab = targetTab
                                            navigateTo(AlphaRoutes.STUDIO)
                                        },
                                        onBookWithoutLogin = { navigateTo(AlphaRoutes.BOOK_AD) },
                                        showTopLanguageToggle = false
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

@Composable
private fun AlphaNavigationDrawerSheet(
    language: AppLanguage,
    isSignedIn: Boolean,
    isAdmin: Boolean,
    currentRoute: String,
    preferredStudioTab: Int,
    userIdentifier: String,
    onNavigate: (route: String, studioTab: Int) -> Unit,
    onJumpToHomeSection: (sectionIndex: Int) -> Unit,
    onToggleLanguage: () -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = NavyMidnight,
        drawerContentColor = IvoryWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Drawer Official ALPHA MEDIA Logo Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlphaMediaLogo(
                    modifier = Modifier.fillMaxWidth(0.78f),
                    cornerRadius = 16.dp,
                    horizontalPadding = 10.dp,
                    verticalPadding = 6.dp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = BilingualContent.TAGLINE,
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlueBright,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isSignedIn) {
                        userIdentifier.ifBlank {
                            BilingualContent.t(language, "ALPHA MEDIA Client", "ALPHA MEDIA క్లయింట్")
                        }
                    } else {
                        BilingualContent.t(language, "Guest Visitor • Booking Open", "గెస్ట్ విజిటర్ • బుకింగ్ అందుబాటులో ఉంది")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateSilver
                )
                if (isAdmin) {
                    Text(
                        text = "Authorized Studio Admin",
                        style = MaterialTheme.typography.labelSmall,
                        color = CinematicRedBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
            Spacer(Modifier.height(4.dp))

            val drawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = ElectricBlue.copy(alpha = 0.25f),
                selectedIconColor = ElectricBlueBright,
                selectedTextColor = IvoryWhite,
                unselectedContainerColor = Color.Transparent,
                unselectedIconColor = SlateSilver,
                unselectedTextColor = IvoryWhite
            )

            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "Home", "హోమ్")) },
                selected = currentRoute == AlphaRoutes.HOME,
                onClick = { onNavigate(AlphaRoutes.HOME, 0) },
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                colors = drawerItemColors
            )
            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "AI Advertisement Services", "AI ప్రకటనల సేవలు")) },
                selected = currentRoute == AlphaRoutes.SERVICES,
                onClick = { onNavigate(AlphaRoutes.SERVICES, 0) },
                icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
                colors = drawerItemColors
            )
            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "Portfolio Gallery", "పోర్ట్‌ఫోలియో గ్యాలరీ")) },
                selected = currentRoute == AlphaRoutes.PORTFOLIO,
                onClick = { onNavigate(AlphaRoutes.PORTFOLIO, 0) },
                icon = { Icon(Icons.Filled.Collections, contentDescription = null) },
                colors = drawerItemColors
            )
            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "Book Ad (No Login Required)", "ప్రకటన బుకింగ్ (లాగిన్ అవసరం లేదు)")) },
                selected = currentRoute == AlphaRoutes.BOOK_AD,
                onClick = { onNavigate(AlphaRoutes.BOOK_AD, 0) },
                icon = { Icon(Icons.Filled.Description, contentDescription = null) },
                colors = drawerItemColors
            )
            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "Client Dashboard", "క్లయింట్ డ్యాష్‌బోర్డ్")) },
                selected = currentRoute == AlphaRoutes.STUDIO && (!isAdmin || preferredStudioTab == 0),
                onClick = { onNavigate(AlphaRoutes.STUDIO, 0) },
                icon = { Icon(Icons.Filled.FolderShared, contentDescription = null) },
                colors = drawerItemColors
            )
            if (isAdmin) {
                NavigationDrawerItem(
                    label = { Text(BilingualContent.t(language, "Admin Dashboard", "అడ్మిన్ డ్యాష్‌బోర్డ్")) },
                    selected = currentRoute == AlphaRoutes.STUDIO && preferredStudioTab == 1,
                    onClick = { onNavigate(AlphaRoutes.STUDIO, 1) },
                    icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null) },
                    colors = drawerItemColors
                )
            }
            NavigationDrawerItem(
                label = { Text(BilingualContent.t(language, "About Us & Contact", "మా గురించి & సంప్రదింపు")) },
                selected = false,
                onClick = { onJumpToHomeSection(4) },
                icon = { Icon(Icons.Filled.ContactPhone, contentDescription = null) },
                colors = drawerItemColors
            )

            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))

            NavigationDrawerItem(
                label = {
                    Text(
                        if (language == AppLanguage.ENGLISH) "Switch to తెలుగు" else "Switch to English"
                    )
                },
                selected = false,
                onClick = onToggleLanguage,
                icon = { Icon(Icons.Filled.Translate, contentDescription = null, tint = ElectricBlueBright) },
                colors = drawerItemColors
            )

            if (!isSignedIn) {
                NavigationDrawerItem(
                    label = {
                        Text(
                            BilingualContent.t(language, "Mobile / Email / Admin Login", "మొబైల్ / ఇమెయిల్ / అడ్మిన్ లాగిన్"),
                            color = ElectricBlueBright
                        )
                    },
                    selected = currentRoute == AlphaRoutes.LOGIN,
                    onClick = { onNavigate(AlphaRoutes.LOGIN, 0) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = ElectricBlueBright) },
                    colors = drawerItemColors
                )
            } else {
                NavigationDrawerItem(
                    label = { Text(BilingualContent.t(language, "Sign Out", "సైన్ అవుట్"), color = CinematicRedBright) },
                    selected = false,
                    onClick = onSignOut,
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = CinematicRedBright) },
                    colors = drawerItemColors
                )
            }

            Spacer(Modifier.weight(1f))
            Text(
                text = BilingualContent.STUDIO_LOCATION_EN,
                style = MaterialTheme.typography.labelSmall,
                color = SlateSilver,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            )
        }
    }
}
