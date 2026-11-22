package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("EN", "English"),
    TELUGU("TE", "తెలుగు")
}

enum class ProjectStatus(val key: String, val labelEn: String, val labelTe: String) {
    NEW("NEW", "New", "కొత్తది"),
    AGREEMENT_PENDING("AGREEMENT_PENDING", "Agreement Pending", "ఒప్పందం పెండింగ్"),
    AGREEMENT_ACCEPTED("AGREEMENT_ACCEPTED", "Agreement Accepted", "ఒప్పందం ఆమోదించబడింది"),
    SCRIPT_APPROVAL("SCRIPT_APPROVAL", "Script Approval", "స్క్రిప్ట్ ఆమోదం"),
    PRODUCTION("PRODUCTION", "Production", "నిర్మాణం"),
    CLIENT_REVIEW("CLIENT_REVIEW", "Client Review", "క్లయింట్ సమీక్ష"),
    FINAL_APPROVAL("FINAL_APPROVAL", "Final Approval", "తుది ఆమోదం"),
    DELIVERED("DELIVERED", "Delivered", "డెలివరీ పూర్తయింది");

    companion object {
        fun fromKey(raw: String?): ProjectStatus =
            entries.firstOrNull { it.key == raw || it.name == raw } ?: NEW
    }
}

enum class PublishingStatus(val key: String, val labelEn: String, val labelTe: String) {
    NOT_AUTHORIZED("NOT_AUTHORIZED", "Not Authorized", "అనుమతి లేదు"),
    PENDING_APPROVAL("PENDING_APPROVAL", "Pending Approval", "ఆమోదం పెండింగ్"),
    AUTHORIZED("AUTHORIZED", "Authorized", "అనుమతించబడింది"),
    REVOKED("REVOKED", "Revoked", "ఉపసంహరించబడింది");

    companion object {
        fun fromKey(raw: String?): PublishingStatus =
            entries.firstOrNull { it.key == raw || it.name == raw } ?: NOT_AUTHORIZED
    }
}

enum class PaymentStatus(val key: String, val labelEn: String, val labelTe: String) {
    UNPAID("UNPAID", "Unpaid", "చెల్లించలేదు"),
    ADVANCE_PAID("ADVANCE_PAID", "Advance Paid", "అడ్వాన్స్ చెల్లించబడింది"),
    PAID_IN_FULL("PAID_IN_FULL", "Paid in Full", "పూర్తి చెల్లింపు");

    companion object {
        fun fromKey(raw: String?): PaymentStatus =
            entries.firstOrNull { it.key == raw || it.name == raw } ?: UNPAID
    }
}

@Keep
@IgnoreExtraProperties
data class BookingAgreement(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val referenceNumber: String = "",
    val agreementReferenceNumber: String = "",
    val clientFullName: String = "",
    val businessName: String = "",
    val businessAddress: String = "",
    val whatsappNumber: String = "",
    val emailAddress: String = "",
    val adTitle: String = "",
    val productServiceName: String = "",
    val adType: String = "",
    val adLanguage: String = "Telugu",
    val videoDuration: String = "",
    val deliverablesCount: Long = 1L,
    val budgetRange: String = "",
    val agreedPrice: String = "",
    val advancePaymentDetails: String = "",
    val projectDescription: String = "",
    val specialInstructions: String = "",
    val referenceMediaSummary: String = "",
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
    val agreementDate: String = "",
    val representativeName: String = "ALPHA MEDIA Studio Desk",
    val projectStatus: String = ProjectStatus.AGREEMENT_ACCEPTED.key,
    val publishingStatus: String = PublishingStatus.NOT_AUTHORIZED.key,
    val paymentStatus: String = PaymentStatus.UNPAID.key,
    val revisionNotes: String = "",
    val deliveryCompleted: Boolean = false,
    val agreementVersion: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BookingHistoryEvent(
    @DocumentId val id: String = "",
    val bookingId: String = "",
    val userId: String = "",
    val actorEmail: String = "",
    val actorRole: String = "CLIENT",
    val actionTitle: String = "",
    val actionDetail: String = "",
    val agreementVersion: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PortfolioItem(
    @DocumentId val id: String = "",
    val titleEn: String = "",
    val titleTe: String = "",
    val category: String = "",
    val descriptionEn: String = "",
    val descriptionTe: String = "",
    val durationLabel: String = "",
    val visualKey: String = "product",
    val storyboardSteps: List<String> = emptyList(),
    val conceptNoticeEn: String = "AI Concept Advertisement — Demonstration showcase only.",
    val conceptNoticeTe: String = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val isBuiltIn: Boolean = false
)

@Keep
@IgnoreExtraProperties
data class AgencyService(
    @DocumentId val id: String = "",
    val orderIndex: Long = 1L,
    val titleEn: String = "",
    val titleTe: String = "",
    val descriptionEn: String = "",
    val descriptionTe: String = "",
    val deliverablesEn: String = "",
    val deliverablesTe: String = "",
    val adTypeKey: String = "AI Video Advertisement",
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BusinessEnquiry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val businessName: String = "",
    val whatsappNumber: String = "",
    val email: String = "",
    val message: String = "",
    val status: String = "OPEN",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class GroundingSource(
    val title: String,
    val url: String
)

sealed interface AiResearchState {
    data object Idle : AiResearchState
    data object Loading : AiResearchState
    data class Success(
        val summary: String,
        val sources: List<GroundingSource>
    ) : AiResearchState
    data class Error(val message: String) : AiResearchState
}

sealed interface AiStrategyState {
    data object Idle : AiStrategyState
    data object Loading : AiStrategyState
    data class Streaming(
        val thoughts: String,
        val answer: String
    ) : AiStrategyState
    data class Success(
        val thoughts: String,
        val answer: String,
        val thoughtsTokenCount: Int?
    ) : AiStrategyState
    data class Error(val message: String) : AiStrategyState
}
