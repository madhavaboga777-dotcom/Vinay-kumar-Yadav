package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AlphaAiRepository
import com.example.data.AlphaRepository
import com.example.data.BilingualContent
import com.example.model.AgencyService
import com.example.model.AiResearchState
import com.example.model.AiStrategyState
import com.example.model.AppLanguage
import com.example.model.BookingAgreement
import com.example.model.BookingHistoryEvent
import com.example.model.BusinessEnquiry
import com.example.model.PaymentStatus
import com.example.model.PortfolioItem
import com.example.model.ProjectStatus
import com.example.model.PublishingStatus
import com.example.model.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BookingFormState(
    val clientFullName: String = "",
    val businessName: String = "",
    val businessAddress: String = "",
    val whatsappNumber: String = "",
    val emailAddress: String = "",
    val adTitle: String = "",
    val productServiceName: String = "",
    val adType: String = BilingualContent.AD_TYPES.first(),
    val adLanguage: String = "Telugu",
    val videoDuration: String = "30 seconds",
    val deliverablesCount: String = "1",
    val budgetRange: String = BilingualContent.BUDGET_RANGES[1],
    val agreedPrice: String = "",
    val advancePaymentDetails: String = "",
    val projectDescription: String = "",
    val specialInstructions: String = "",
    val referenceMediaNames: List<String> = emptyList(),
    val expectedDeliveryDate: String = "",
    val productionAgreementAccepted: Boolean = false,
    val aiContentDisclosureAccepted: Boolean = false,
    val realPersonVoiceConsentAccepted: Boolean = false,
    val clientApprovalChecked: Boolean = false,
    val clientFinalApproved: Boolean = false,
    val clientApprovalAccuracy: Boolean = false,
    val publishClientSocial: Boolean = false,
    val publishAgencySocial: Boolean = false,
    val publishAgencyPortfolio: Boolean = false,
    val publishDoNotPublish: Boolean = true,
    val clientSignature: String = "",
    val agreementDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
    val representativeName: String = "ALPHA MEDIA Studio Desk",
    val referenceNumber: String = "",
    val agreementReferenceNumber: String = "",
    val validationError: String? = null,
    val isSubmitting: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class AlphaViewModel(
    private val repository: AlphaRepository,
    private val aiRepository: AlphaAiRepository,
    private val currentUserId: String,
    private val currentUserEmail: String,
    private val currentUserPhone: String,
    private val currentUserDisplayName: String
) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _lastSubmittedBooking = MutableStateFlow<BookingAgreement?>(null)
    val lastSubmittedBooking: StateFlow<BookingAgreement?> = _lastSubmittedBooking.asStateFlow()

    private val _bookingForm = MutableStateFlow(
        BookingFormState(
            clientFullName = currentUserDisplayName,
            whatsappNumber = currentUserPhone,
            emailAddress = currentUserEmail,
            clientSignature = currentUserDisplayName,
            referenceNumber = generateRef("AM"),
            agreementReferenceNumber = generateRef("AGR")
        )
    )
    val bookingForm: StateFlow<BookingFormState> = _bookingForm.asStateFlow()

    private val _aiResearchState = MutableStateFlow<AiResearchState>(AiResearchState.Idle)
    val aiResearchState: StateFlow<AiResearchState> = _aiResearchState.asStateFlow()

    private val _aiStrategyState = MutableStateFlow<AiStrategyState>(AiStrategyState.Idle)
    val aiStrategyState: StateFlow<AiStrategyState> = _aiStrategyState.asStateFlow()

    private val _selectedHistoryBooking = MutableStateFlow<BookingAgreement?>(null)

    val myBookingsState: StateFlow<UiState<List<BookingAgreement>>> = if (currentUserId.isBlank()) {
        MutableStateFlow<UiState<List<BookingAgreement>>>(UiState.Success(emptyList())).asStateFlow()
    } else {
        repository
            .observeMyBookings(currentUserId)
            .map<List<BookingAgreement>, UiState<List<BookingAgreement>>> { UiState.Success(it) }
            .catch { error -> emit(unwrapFirestoreError("observeMyBookings", error)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )
    }

    val adminBookingsState: StateFlow<UiState<List<BookingAgreement>>> = _isAdmin
        .flatMapLatest { admin ->
            if (!admin || currentUserId.isBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observeAllBookingsForAdmin()
                    .map<List<BookingAgreement>, UiState<List<BookingAgreement>>> { UiState.Success(it) }
                    .catch { error -> emit(unwrapFirestoreError("observeAllBookingsForAdmin", error)) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Loading
        )

    val adminEnquiriesState: StateFlow<UiState<List<BusinessEnquiry>>> = _isAdmin
        .flatMapLatest { admin ->
            when {
                currentUserId.isBlank() -> flowOf(UiState.Success(emptyList()))
                !admin -> repository.observeMyEnquiries(currentUserId)
                    .map<List<BusinessEnquiry>, UiState<List<BusinessEnquiry>>> { UiState.Success(it) }
                    .catch { error -> emit(unwrapFirestoreError("observeMyEnquiries", error)) }
                else -> repository.observeAllEnquiriesForAdmin()
                    .map<List<BusinessEnquiry>, UiState<List<BusinessEnquiry>>> { UiState.Success(it) }
                    .catch { error -> emit(unwrapFirestoreError("observeAllEnquiriesForAdmin", error)) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Loading
        )

    val portfolioState: StateFlow<UiState<List<PortfolioItem>>> = repository
        .observePortfolioItems()
        .map<List<PortfolioItem>, UiState<List<PortfolioItem>>> { remote ->
            UiState.Success(remote + BilingualContent.defaultPortfolioItems)
        }
        .catch { error ->
            Log.w(TAG, "Remote portfolio fallback to defaults", error)
            emit(UiState.Success(BilingualContent.defaultPortfolioItems))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Success(BilingualContent.defaultPortfolioItems)
        )

    val servicesState: StateFlow<UiState<List<AgencyService>>> = repository
        .observeServices()
        .map<List<AgencyService>, UiState<List<AgencyService>>> { remote ->
            val combined = if (remote.isEmpty()) BilingualContent.defaultServices else remote
            UiState.Success(combined)
        }
        .catch { error ->
            Log.w(TAG, "Remote services fallback to defaults", error)
            emit(UiState.Success(BilingualContent.defaultServices))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Success(BilingualContent.defaultServices)
        )

    val selectedBookingHistoryState: StateFlow<UiState<List<BookingHistoryEvent>>> = _selectedHistoryBooking
        .flatMapLatest { booking ->
            if (booking == null || booking.id.isBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                repository.observeBookingHistory(booking.id, booking.userId, _isAdmin.value)
                    .map<List<BookingHistoryEvent>, UiState<List<BookingHistoryEvent>>> { UiState.Success(it) }
                    .catch { error -> emit(unwrapFirestoreError("observeBookingHistory", error)) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = UiState.Success(emptyList())
        )

    init {
        refreshAdminStatus()
    }

    fun refreshAdminStatus() {
        viewModelScope.launch {
            _isAdmin.value = if (currentUserId.isBlank()) false else repository.checkIsAdmin()
        }
    }

    fun toggleLanguage() {
        _language.update { if (it == AppLanguage.ENGLISH) AppLanguage.TELUGU else AppLanguage.ENGLISH }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearLastSubmittedBooking() {
        _lastSubmittedBooking.value = null
    }

    fun selectBookingForHistory(booking: BookingAgreement?) {
        _selectedHistoryBooking.value = booking
    }

    fun updateBookingForm(transform: (BookingFormState) -> BookingFormState) {
        _bookingForm.update { transform(it).copy(validationError = null) }
    }

    fun prefillBookingFromService(service: AgencyService) {
        _bookingForm.update {
            it.copy(
                adType = service.adTypeKey,
                adTitle = service.titleEn,
                projectDescription = if (it.projectDescription.isBlank()) service.descriptionEn else it.projectDescription,
                validationError = null
            )
        }
    }

    fun prefillBookingFromPortfolio(item: PortfolioItem) {
        _bookingForm.update {
            it.copy(
                adTitle = "Custom Campaign inspired by ${item.titleEn}",
                adType = BilingualContent.AD_TYPES.first(),
                projectDescription = "Concept style: ${item.category}. ${item.descriptionEn}",
                validationError = null
            )
        }
    }

    fun prefillBookingFromAiStrategy(
        businessName: String,
        product: String,
        adType: String,
        adLanguage: String,
        duration: String,
        generatedBrief: String
    ) {
        _bookingForm.update {
            it.copy(
                businessName = businessName.ifBlank { it.businessName },
                productServiceName = product.ifBlank { it.productServiceName },
                adTitle = if (product.isNotBlank()) "$product AI Campaign" else it.adTitle,
                adType = adType,
                adLanguage = adLanguage,
                videoDuration = duration,
                projectDescription = generatedBrief.take(1950),
                validationError = null
            )
        }
    }

    fun submitBookingAndAgreement() {
        val form = _bookingForm.value
        val isTe = _language.value == AppLanguage.TELUGU

        val errorMsg = when {
            form.clientFullName.trim().length < 2 ->
                if (isTe) "దయచేసి క్లయింట్ పూర్తి పేరు నమోదు చేయండి." else "Please enter client full name (min 2 characters)."
            form.businessName.trim().length < 2 ->
                if (isTe) "దయచేసి వ్యాపారం పేరు నమోదు చేయండి." else "Please enter business/company name."
            form.businessAddress.trim().length < 3 ->
                if (isTe) "దయచేసి వ్యాపార చిరునామా నమోదు చేయండి." else "Please enter business address."
            form.whatsappNumber.trim().length < 7 ->
                if (isTe) "దయచేసి సరైన వాట్సాప్ నంబర్ నమోదు చేయండి." else "Please enter a valid WhatsApp number."
            form.adTitle.trim().length < 2 ->
                if (isTe) "దయచేసి ప్రకటన శీర్షిక నమోదు చేయండి." else "Please enter an advertisement title."
            form.productServiceName.trim().length < 2 ->
                if (isTe) "దయచేసి ఉత్పత్తి లేదా సేవ పేరు నమోదు చేయండి." else "Please enter product/service name."
            form.projectDescription.trim().length < 5 ->
                if (isTe) "దయచేసి ప్రాజెక్ట్ వివరణ (కనీసం 5 అక్షరాలు) నమోదు చేయండి." else "Please enter a project description (min 5 characters)."
            form.expectedDeliveryDate.trim().length < 4 ->
                if (isTe) "దయచేసి ఆశించిన డెలివరీ తేదీని ఎంచుకోండి." else "Please select an expected delivery date."
            !form.productionAgreementAccepted ->
                if (isTe) "దయచేసి AI ప్రకటన నిర్మాణ ఒప్పందాన్ని ఆమోదించండి." else "Please accept the 10-point AI Advertisement Production Agreement."
            !form.aiContentDisclosureAccepted || !form.realPersonVoiceConsentAccepted ->
                if (isTe) "దయచేసి AI కంటెంట్ ప్రకటన మరియు వాయిస్ అనుమతి చెక్‌బాక్స్‌లను ఎంచుకోండి." else "Please confirm both AI Content Disclosure checkboxes."
            !form.clientApprovalChecked ->
                if (isTe) "దయచేసి క్లయింట్ ఆమోద చెక్‌బాక్స్ ఎంచుకోండి." else "Please confirm the client approval & accuracy checkbox."
            form.clientSignature.trim().length < 2 ->
                if (isTe) "దయచేసి మీ డిజిటల్ సంతకం (పూర్తి పేరు) నమోదు చేయండి." else "Please enter your digital signature (typed full name)."
            else -> null
        }

        if (errorMsg != null) {
            _bookingForm.update { it.copy(validationError = errorMsg) }
            return
        }

        val deliverables = (form.deliverablesCount.toLongOrNull() ?: 1L).coerceIn(1L, 500L)
        val mediaSummary = if (form.referenceMediaNames.isEmpty()) {
            "No reference media attached"
        } else {
            "${form.referenceMediaNames.size} media file(s) selected via Android Photo Picker: " +
                form.referenceMediaNames.take(3).joinToString(", ")
        }.take(500)

        val anyPublish = !form.publishDoNotPublish &&
            (form.publishClientSocial || form.publishAgencySocial || form.publishAgencyPortfolio)

        val draft = BookingAgreement(
            referenceNumber = form.referenceNumber.ifBlank { generateRef("AM") },
            agreementReferenceNumber = form.agreementReferenceNumber.ifBlank { generateRef("AGR") },
            clientFullName = form.clientFullName,
            businessName = form.businessName,
            businessAddress = form.businessAddress,
            whatsappNumber = form.whatsappNumber,
            emailAddress = form.emailAddress,
            adTitle = form.adTitle,
            productServiceName = form.productServiceName,
            adType = form.adType,
            adLanguage = form.adLanguage,
            videoDuration = form.videoDuration,
            deliverablesCount = deliverables,
            budgetRange = form.budgetRange,
            agreedPrice = form.agreedPrice,
            advancePaymentDetails = form.advancePaymentDetails,
            projectDescription = form.projectDescription,
            specialInstructions = form.specialInstructions,
            referenceMediaSummary = mediaSummary,
            expectedDeliveryDate = form.expectedDeliveryDate,
            productionAgreementAccepted = true,
            aiContentDisclosureAccepted = true,
            realPersonVoiceConsentAccepted = true,
            clientApprovalChecked = form.clientApprovalChecked,
            clientFinalApproved = form.clientFinalApproved,
            clientApprovalAccuracy = form.clientApprovalAccuracy,
            publishClientSocial = if (anyPublish) form.publishClientSocial else false,
            publishAgencySocial = if (anyPublish) form.publishAgencySocial else false,
            publishAgencyPortfolio = if (anyPublish) form.publishAgencyPortfolio else false,
            publishDoNotPublish = !anyPublish,
            clientSignature = form.clientSignature,
            agreementDate = form.agreementDate,
            representativeName = form.representativeName
        )

        viewModelScope.launch {
            _bookingForm.update { it.copy(isSubmitting = true, validationError = null) }
            val result = repository.createBookingWithAgreement(draft)
            result.fold(
                onSuccess = { saved ->
                    _lastSubmittedBooking.value = saved
                    _bookingForm.value = BookingFormState(
                        clientFullName = currentUserDisplayName,
                        whatsappNumber = currentUserPhone,
                        emailAddress = currentUserEmail,
                        clientSignature = currentUserDisplayName,
                        referenceNumber = generateRef("AM"),
                        agreementReferenceNumber = generateRef("AGR")
                    )
                    _snackbarMessage.value = if (isTe) {
                        "బుకింగ్ & ఒప్పందం విజయవంతంగా సేవ్ చేయబడ్డాయి (${saved.referenceNumber})"
                    } else {
                        "Booking & Digital Agreement saved (${saved.referenceNumber})"
                    }
                },
                onFailure = { err ->
                    _bookingForm.update {
                        it.copy(
                            isSubmitting = false,
                            validationError = err.localizedMessage ?: "Failed to save booking."
                        )
                    }
                }
            )
        }
    }

    fun updatePublishingRights(
        booking: BookingAgreement,
        publishClientSocial: Boolean,
        publishAgencySocial: Boolean,
        publishAgencyPortfolio: Boolean,
        publishDoNotPublish: Boolean
    ) {
        viewModelScope.launch {
            val result = repository.updatePublishingRights(
                booking, publishClientSocial, publishAgencySocial, publishAgencyPortfolio, publishDoNotPublish
            )
            result.fold(
                onSuccess = { _snackbarMessage.value = "Publishing rights updated (v${booking.agreementVersion + 1})." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Failed to update publishing rights." }
            )
        }
    }

    fun submitClientRevisionOrApproval(
        booking: BookingAgreement,
        clientFinalApproved: Boolean,
        clientApprovalAccuracy: Boolean,
        revisionNotes: String,
        specialInstructions: String
    ) {
        viewModelScope.launch {
            val result = repository.submitClientRevisionOrApproval(
                booking, clientFinalApproved, clientApprovalAccuracy, revisionNotes, specialInstructions
            )
            result.fold(
                onSuccess = { _snackbarMessage.value = "Approval / revision record saved." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Failed to update booking." }
            )
        }
    }

    fun adminUpdateBooking(
        booking: BookingAgreement,
        projectStatus: ProjectStatus,
        publishingStatus: PublishingStatus,
        paymentStatus: PaymentStatus,
        agreedPrice: String,
        advancePaymentDetails: String,
        representativeName: String,
        revisionNotes: String,
        deliveryCompleted: Boolean
    ) {
        viewModelScope.launch {
            val result = repository.adminUpdateBooking(
                booking, projectStatus, publishingStatus, paymentStatus,
                agreedPrice, advancePaymentDetails, representativeName, revisionNotes, deliveryCompleted
            )
            result.fold(
                onSuccess = { _snackbarMessage.value = "Admin updated ${booking.referenceNumber}." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Admin update failed." }
            )
        }
    }

    fun adminAddPortfolioItem(
        titleEn: String,
        titleTe: String,
        category: String,
        descriptionEn: String,
        descriptionTe: String,
        durationLabel: String,
        visualKey: String,
        storyboardRaw: String
    ) {
        if (titleEn.trim().length < 2 || descriptionEn.trim().length < 5) {
            _snackbarMessage.value = "Please enter a valid portfolio title and description."
            return
        }
        val steps = storyboardRaw.lines().map { it.trim() }.filter { it.isNotBlank() }.take(8)
            .ifEmpty { listOf("Opening hook", "Product showcase", "Call to action") }
        viewModelScope.launch {
            val result = repository.adminSavePortfolioItem(
                titleEn, titleTe, category, descriptionEn, descriptionTe, durationLabel, visualKey, steps
            )
            result.fold(
                onSuccess = { _snackbarMessage.value = "Portfolio item published." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Failed to add portfolio item." }
            )
        }
    }

    fun adminDeletePortfolioItem(itemId: String) {
        viewModelScope.launch {
            repository.adminDeletePortfolioItem(itemId).fold(
                onSuccess = { _snackbarMessage.value = "Portfolio item removed." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Delete failed." }
            )
        }
    }

    fun adminAddService(
        orderIndex: Long,
        titleEn: String,
        titleTe: String,
        descriptionEn: String,
        descriptionTe: String,
        deliverablesEn: String,
        deliverablesTe: String,
        adTypeKey: String
    ) {
        if (titleEn.trim().length < 2 || descriptionEn.trim().length < 5) {
            _snackbarMessage.value = "Please enter valid service title and description."
            return
        }
        viewModelScope.launch {
            repository.adminSaveService(
                orderIndex, titleEn, titleTe, descriptionEn, descriptionTe, deliverablesEn, deliverablesTe, adTypeKey
            ).fold(
                onSuccess = { _snackbarMessage.value = "Service added." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Failed to add service." }
            )
        }
    }

    fun adminDeleteService(serviceId: String) {
        viewModelScope.launch {
            repository.adminDeleteService(serviceId).fold(
                onSuccess = { _snackbarMessage.value = "Service removed." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Delete failed." }
            )
        }
    }

    fun submitBusinessEnquiry(
        name: String,
        businessName: String,
        whatsappNumber: String,
        email: String,
        message: String
    ) {
        if (name.trim().length < 2 || businessName.trim().length < 2 ||
            whatsappNumber.trim().length < 7 || message.trim().length < 5
        ) {
            _snackbarMessage.value = "Please complete all required enquiry fields."
            return
        }
        viewModelScope.launch {
            repository.submitEnquiry(name, businessName, whatsappNumber, email, message).fold(
                onSuccess = { _snackbarMessage.value = "Enquiry sent to ALPHA MEDIA Pithapuram Desk!" },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Enquiry submission failed." }
            )
        }
    }

    fun adminMarkEnquiryResponded(enquiryId: String) {
        viewModelScope.launch {
            repository.adminMarkEnquiryResponded(enquiryId).fold(
                onSuccess = { _snackbarMessage.value = "Enquiry marked responded." },
                onFailure = { _snackbarMessage.value = it.localizedMessage ?: "Update failed." }
            )
        }
    }

    // --- FIREBASE AI LOGIC: 1. GOOGLE SEARCH GROUNDING ---
    fun runSearchGroundedTrendResearch(
        businessType: String,
        regionOrLanguage: String,
        goal: String
    ) {
        if (businessType.isBlank()) {
            _aiResearchState.value = AiResearchState.Error("Please enter a business type (e.g., Dairy, Silk Sarees, Restaurant).")
            return
        }
        viewModelScope.launch {
            _aiResearchState.value = AiResearchState.Loading
            val res = aiRepository.researchAdTrendsWithGoogleSearch(
                businessType = businessType,
                regionOrLanguage = regionOrLanguage.ifBlank { "Andhra Pradesh / Telugu & English" },
                goal = goal.ifBlank { "Local brand awareness and WhatsApp inquiries" }
            )
            res.fold(
                onSuccess = { data ->
                    _aiResearchState.value = AiResearchState.Success(
                        summary = data.summary,
                        sources = data.sources
                    )
                },
                onFailure = { err ->
                    _aiResearchState.value = AiResearchState.Error(
                        err.localizedMessage ?: "Google Search grounded research failed."
                    )
                }
            )
        }
    }

    // --- FIREBASE AI LOGIC: 2. HIGH THINKING MODE CREATIVE STRATEGIST ---
    fun generateDeepCreativeStrategy(
        businessName: String,
        productOrService: String,
        adType: String,
        adLanguage: String,
        duration: String,
        audienceNotes: String
    ) {
        if (businessName.isBlank() || productOrService.isBlank()) {
            _aiStrategyState.value = AiStrategyState.Error("Please provide both Business Name and Product/Service.")
            return
        }
        viewModelScope.launch {
            _aiStrategyState.value = AiStrategyState.Loading
            try {
                aiRepository.streamDeepCreativeStrategy(
                    businessName = businessName,
                    productOrService = productOrService,
                    adType = adType,
                    adLanguage = adLanguage,
                    duration = duration,
                    audienceNotes = audienceNotes
                ).collect { chunk ->
                    _aiStrategyState.value = if (chunk.isFinal) {
                        AiStrategyState.Success(
                            thoughts = chunk.thoughts,
                            answer = chunk.answer,
                            thoughtsTokenCount = chunk.thoughtsTokenCount
                        )
                    } else {
                        AiStrategyState.Streaming(
                            thoughts = chunk.thoughts,
                            answer = chunk.answer
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Deep creative strategy generation failed", e)
                _aiStrategyState.value = AiStrategyState.Error(
                    e.localizedMessage ?: "Deep strategy generation failed."
                )
            }
        }
    }

    private fun unwrapFirestoreError(op: String, error: Throwable): UiState.Error {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error in $op", rootError)
        return UiState.Error(rootError.localizedMessage ?: "Failed to load data.")
    }

    private fun generateRef(prefix: String): String {
        val dateStamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val code = (1000..9999).random()
        return "$prefix-$dateStamp-$code"
    }

    companion object {
        private const val TAG = "AlphaVM"
        private const val STOP_TIMEOUT_MS = 5000L
    }
}
