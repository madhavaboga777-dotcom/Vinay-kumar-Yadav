package com.example.data

import android.content.Context
import com.example.R
import com.example.model.AgencyService
import com.example.model.BookingAgreement
import com.example.model.BookingHistoryEvent
import com.example.model.BusinessEnquiry
import com.example.model.PaymentStatus
import com.example.model.PortfolioItem
import com.example.model.ProjectStatus
import com.example.model.PublishingStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AlphaRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        const val BOOTSTRAP_ADMIN_EMAIL = "madhavaboga777@gmail.com"
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in before accessing your private studio records.")
    }

    private fun currentActorIdentifier(fallback: String = ""): String {
        val user = auth.currentUser
        return user?.email?.takeIf { it.isNotBlank() }
            ?: user?.phoneNumber?.takeIf { it.isNotBlank() }
            ?: fallback
    }

    suspend fun checkIsAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        if (user.isEmailVerified && user.email.equals(BOOTSTRAP_ADMIN_EMAIL, ignoreCase = true)) {
            return true
        }
        return try {
            val doc = db.collection("admins").document(user.uid).get().await()
            doc.exists()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    fun observeMyBookings(userId: String): Flow<List<BookingAgreement>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Authenticated UID mismatch." }
        val path = "bookings"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BookingAgreement::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeAllBookingsForAdmin(): Flow<List<BookingAgreement>> = flow {
        val uid = requireUserId()
        val path = "bookings"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BookingAgreement::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeBookingHistory(bookingId: String, ownerUserId: String, isAdmin: Boolean): Flow<List<BookingHistoryEvent>> = flow {
        val uid = requireUserId()
        val path = "bookings/$bookingId/history"
        val query: Query = if (isAdmin) {
            db.collection("bookings").document(bookingId).collection("history")
        } else {
            db.collection("bookings").document(bookingId).collection("history")
                .whereEqualTo("userId", ownerUserId)
        }
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BookingHistoryEvent::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observePortfolioItems(): Flow<List<PortfolioItem>> = flow {
        val path = "portfolio_items"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        PortfolioItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeServices(): Flow<List<AgencyService>> = flow {
        val path = "services"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        AgencyService::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.orderIndex }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeMyEnquiries(userId: String): Flow<List<BusinessEnquiry>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "Authenticated UID mismatch." }
        val path = "enquiries"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BusinessEnquiry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeAllEnquiriesForAdmin(): Flow<List<BusinessEnquiry>> = flow {
        val uid = requireUserId()
        val path = "enquiries"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BusinessEnquiry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createBookingWithAgreement(draft: BookingAgreement): Result<BookingAgreement> {
        var docPath = "bookings"
        return try {
            val uid = auth.currentUser?.uid?.takeIf { it.isNotBlank() } ?: "guest"
            val userEmail = currentActorIdentifier(
                fallback = draft.emailAddress.trim().ifBlank { draft.whatsappNumber.trim() }
            ).take(160)
            val dateStamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val suffix = UUID.randomUUID().toString().take(4).uppercase(Locale.US)
            val refNumber = draft.referenceNumber.ifBlank { "AM-$dateStamp-$suffix" }
            val agrNumber = draft.agreementReferenceNumber.ifBlank { "AGR-$dateStamp-$suffix" }

            val anyPublishGranted = !draft.publishDoNotPublish &&
                (draft.publishClientSocial || draft.publishAgencySocial || draft.publishAgencyPortfolio)
            val computedPublishingStatus = if (anyPublishGranted) {
                PublishingStatus.AUTHORIZED.key
            } else {
                PublishingStatus.NOT_AUTHORIZED.key
            }

            val bookingDoc = db.collection("bookings").document()
            docPath = bookingDoc.path

            val payload = mapOf(
                "userId" to uid,
                "userEmail" to userEmail,
                "referenceNumber" to refNumber,
                "agreementReferenceNumber" to agrNumber,
                "clientFullName" to draft.clientFullName.trim().take(120),
                "businessName" to draft.businessName.trim().take(160),
                "businessAddress" to draft.businessAddress.trim().take(300),
                "whatsappNumber" to draft.whatsappNumber.trim().take(25),
                "emailAddress" to draft.emailAddress.trim().take(160),
                "adTitle" to draft.adTitle.trim().take(160),
                "productServiceName" to draft.productServiceName.trim().take(160),
                "adType" to draft.adType.trim().take(80),
                "adLanguage" to draft.adLanguage.trim().take(40),
                "videoDuration" to draft.videoDuration.trim().take(60),
                "deliverablesCount" to draft.deliverablesCount.coerceIn(1L, 500L),
                "budgetRange" to draft.budgetRange.trim().take(80),
                "agreedPrice" to draft.agreedPrice.trim().take(80),
                "advancePaymentDetails" to draft.advancePaymentDetails.trim().take(300),
                "projectDescription" to draft.projectDescription.trim().take(2000),
                "specialInstructions" to draft.specialInstructions.trim().take(1000),
                "referenceMediaSummary" to draft.referenceMediaSummary.trim().take(500),
                "expectedDeliveryDate" to draft.expectedDeliveryDate.trim().take(40),
                "productionAgreementAccepted" to true,
                "aiContentDisclosureAccepted" to true,
                "realPersonVoiceConsentAccepted" to true,
                "clientApprovalChecked" to draft.clientApprovalChecked,
                "clientFinalApproved" to draft.clientFinalApproved,
                "clientApprovalAccuracy" to draft.clientApprovalAccuracy,
                "publishClientSocial" to (if (draft.publishDoNotPublish) false else draft.publishClientSocial),
                "publishAgencySocial" to (if (draft.publishDoNotPublish) false else draft.publishAgencySocial),
                "publishAgencyPortfolio" to (if (draft.publishDoNotPublish) false else draft.publishAgencyPortfolio),
                "publishDoNotPublish" to (!anyPublishGranted || draft.publishDoNotPublish),
                "clientSignature" to draft.clientSignature.trim().take(120),
                "agreementDate" to draft.agreementDate.trim().take(40),
                "representativeName" to draft.representativeName.trim().ifBlank { "ALPHA MEDIA Studio Desk" }.take(120),
                "projectStatus" to ProjectStatus.AGREEMENT_ACCEPTED.key,
                "publishingStatus" to computedPublishingStatus,
                "paymentStatus" to PaymentStatus.UNPAID.key,
                "revisionNotes" to "",
                "deliveryCompleted" to false,
                "agreementVersion" to 1L,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val historyDoc = bookingDoc.collection("history").document()
            val historyPayload = mapOf(
                "bookingId" to bookingDoc.id,
                "userId" to uid,
                "actorEmail" to userEmail,
                "actorRole" to "CLIENT",
                "actionTitle" to "Booking & Digital Agreement Submitted",
                "actionDetail" to "Ref $refNumber | Agreement $agrNumber | Publishing: $computedPublishingStatus",
                "agreementVersion" to 1L,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.runBatch { batch ->
                batch.set(bookingDoc, payload)
                batch.set(historyDoc, historyPayload)
            }.await()

            Result.success(
                draft.copy(
                    id = bookingDoc.id,
                    userId = uid,
                    userEmail = userEmail,
                    referenceNumber = refNumber,
                    agreementReferenceNumber = agrNumber,
                    projectStatus = ProjectStatus.AGREEMENT_ACCEPTED.key,
                    publishingStatus = computedPublishingStatus,
                    paymentStatus = PaymentStatus.UNPAID.key,
                    agreementVersion = 1L
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updatePublishingRights(
        booking: BookingAgreement,
        publishClientSocial: Boolean,
        publishAgencySocial: Boolean,
        publishAgencyPortfolio: Boolean,
        publishDoNotPublish: Boolean
    ): Result<Unit> {
        val path = "bookings/${booking.id}"
        return try {
            val uid = requireUserId()
            val userEmail = currentActorIdentifier().take(160)
            val hasAny = !publishDoNotPublish && (publishClientSocial || publishAgencySocial || publishAgencyPortfolio)
            val newStatus = if (hasAny) {
                PublishingStatus.AUTHORIZED.key
            } else if (booking.publishingStatus == PublishingStatus.AUTHORIZED.key || publishDoNotPublish) {
                PublishingStatus.REVOKED.key
            } else {
                PublishingStatus.NOT_AUTHORIZED.key
            }
            val nextVersion = booking.agreementVersion + 1L

            val bookingRef = db.collection("bookings").document(booking.id)
            val historyRef = bookingRef.collection("history").document()

            db.runBatch { batch ->
                batch.update(
                    bookingRef,
                    mapOf(
                        "publishClientSocial" to (if (hasAny) publishClientSocial else false),
                        "publishAgencySocial" to (if (hasAny) publishAgencySocial else false),
                        "publishAgencyPortfolio" to (if (hasAny) publishAgencyPortfolio else false),
                        "publishDoNotPublish" to !hasAny,
                        "publishingStatus" to newStatus,
                        "agreementVersion" to nextVersion,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.set(
                    historyRef,
                    mapOf(
                        "bookingId" to booking.id,
                        "userId" to booking.userId,
                        "actorEmail" to userEmail,
                        "actorRole" to if (uid == booking.userId) "CLIENT" else "ADMIN",
                        "actionTitle" to "Publishing Rights Updated: $newStatus",
                        "actionDetail" to "ClientSocial=${hasAny && publishClientSocial}, AgencySocial=${hasAny && publishAgencySocial}, Portfolio=${hasAny && publishAgencyPortfolio}",
                        "agreementVersion" to nextVersion,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun submitClientRevisionOrApproval(
        booking: BookingAgreement,
        clientFinalApproved: Boolean,
        clientApprovalAccuracy: Boolean,
        revisionNotes: String,
        specialInstructions: String
    ): Result<Unit> {
        val path = "bookings/${booking.id}"
        return try {
            requireUserId()
            val userEmail = currentActorIdentifier().take(160)
            val nextVersion = booking.agreementVersion + 1L
            val nextProjectStatus = if (clientFinalApproved && clientApprovalAccuracy) {
                ProjectStatus.FINAL_APPROVAL.key
            } else {
                booking.projectStatus
            }

            val bookingRef = db.collection("bookings").document(booking.id)
            val historyRef = bookingRef.collection("history").document()

            db.runBatch { batch ->
                batch.update(
                    bookingRef,
                    mapOf(
                        "clientFinalApproved" to clientFinalApproved,
                        "clientApprovalAccuracy" to clientApprovalAccuracy,
                        "revisionNotes" to revisionNotes.trim().take(1000),
                        "specialInstructions" to specialInstructions.trim().take(1000),
                        "projectStatus" to nextProjectStatus,
                        "agreementVersion" to nextVersion,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.set(
                    historyRef,
                    mapOf(
                        "bookingId" to booking.id,
                        "userId" to booking.userId,
                        "actorEmail" to userEmail,
                        "actorRole" to "CLIENT",
                        "actionTitle" to if (clientFinalApproved) "Client Final Approval Confirmed" else "Client Correction / Revision Requested",
                        "actionDetail" to revisionNotes.trim().ifBlank { "Updated instructions / approval." }.take(500),
                        "agreementVersion" to nextVersion,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateBooking(
        booking: BookingAgreement,
        projectStatus: ProjectStatus,
        publishingStatus: PublishingStatus,
        paymentStatus: PaymentStatus,
        agreedPrice: String,
        advancePaymentDetails: String,
        representativeName: String,
        revisionNotes: String,
        deliveryCompleted: Boolean
    ): Result<Unit> {
        val path = "bookings/${booking.id}"
        return try {
            requireUserId()
            val adminEmail = currentActorIdentifier("admin").take(160)
            val nextVersion = booking.agreementVersion + 1L
            val effectivePublishingStatus = if (
                booking.publishDoNotPublish &&
                publishingStatus == PublishingStatus.AUTHORIZED
            ) {
                PublishingStatus.NOT_AUTHORIZED.key
            } else {
                publishingStatus.key
            }

            val bookingRef = db.collection("bookings").document(booking.id)
            val historyRef = bookingRef.collection("history").document()

            db.runBatch { batch ->
                batch.update(
                    bookingRef,
                    mapOf(
                        "projectStatus" to projectStatus.key,
                        "publishingStatus" to effectivePublishingStatus,
                        "paymentStatus" to paymentStatus.key,
                        "agreedPrice" to agreedPrice.trim().take(80),
                        "advancePaymentDetails" to advancePaymentDetails.trim().take(300),
                        "representativeName" to representativeName.trim().ifBlank { "ALPHA MEDIA Studio Desk" }.take(120),
                        "revisionNotes" to revisionNotes.trim().take(1000),
                        "deliveryCompleted" to (deliveryCompleted || projectStatus == ProjectStatus.DELIVERED),
                        "agreementVersion" to nextVersion,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.set(
                    historyRef,
                    mapOf(
                        "bookingId" to booking.id,
                        "userId" to booking.userId,
                        "actorEmail" to adminEmail,
                        "actorRole" to "ADMIN",
                        "actionTitle" to "Admin Updated Status: ${projectStatus.key}",
                        "actionDetail" to "Payment: ${paymentStatus.key} | Publishing: $effectivePublishingStatus | Delivered: ${deliveryCompleted || projectStatus == ProjectStatus.DELIVERED}",
                        "agreementVersion" to nextVersion,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminSavePortfolioItem(
        titleEn: String,
        titleTe: String,
        category: String,
        descriptionEn: String,
        descriptionTe: String,
        durationLabel: String,
        visualKey: String,
        storyboardSteps: List<String>
    ): Result<Unit> {
        var path = "portfolio_items"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("portfolio_items").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "titleEn" to titleEn.trim().take(120),
                    "titleTe" to titleTe.trim().ifBlank { titleEn.trim() }.take(160),
                    "category" to category.trim().take(80),
                    "descriptionEn" to descriptionEn.trim().take(800),
                    "descriptionTe" to descriptionTe.trim().ifBlank { descriptionEn.trim() }.take(1000),
                    "durationLabel" to durationLabel.trim().ifBlank { "30s Concept" }.take(40),
                    "visualKey" to visualKey.trim().ifBlank { "product" }.take(60),
                    "storyboardSteps" to storyboardSteps.take(8),
                    "conceptNoticeEn" to "AI Concept Advertisement — Demonstration showcase only.",
                    "conceptNoticeTe" to "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminDeletePortfolioItem(itemId: String): Result<Unit> {
        val path = "portfolio_items/$itemId"
        return try {
            requireUserId()
            db.collection("portfolio_items").document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun adminSaveService(
        orderIndex: Long,
        titleEn: String,
        titleTe: String,
        descriptionEn: String,
        descriptionTe: String,
        deliverablesEn: String,
        deliverablesTe: String,
        adTypeKey: String
    ): Result<Unit> {
        var path = "services"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("services").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "orderIndex" to orderIndex.coerceIn(1L, 100L),
                    "titleEn" to titleEn.trim().take(120),
                    "titleTe" to titleTe.trim().ifBlank { titleEn.trim() }.take(160),
                    "descriptionEn" to descriptionEn.trim().take(600),
                    "descriptionTe" to descriptionTe.trim().ifBlank { descriptionEn.trim() }.take(800),
                    "deliverablesEn" to deliverablesEn.trim().ifBlank { "Custom Deliverables" }.take(200),
                    "deliverablesTe" to deliverablesTe.trim().ifBlank { deliverablesEn.trim() }.take(200),
                    "adTypeKey" to adTypeKey.trim().ifBlank { "AI Video Advertisement" }.take(80),
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminDeleteService(serviceId: String): Result<Unit> {
        val path = "services/$serviceId"
        return try {
            requireUserId()
            db.collection("services").document(serviceId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun submitEnquiry(
        name: String,
        businessName: String,
        whatsappNumber: String,
        email: String,
        message: String
    ): Result<Unit> {
        var path = "enquiries"
        return try {
            val uid = auth.currentUser?.uid?.takeIf { it.isNotBlank() } ?: "guest"
            val docRef = db.collection("enquiries").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "name" to name.trim().take(120),
                    "businessName" to businessName.trim().take(160),
                    "whatsappNumber" to whatsappNumber.trim().take(25),
                    "email" to email.trim().take(160),
                    "message" to message.trim().take(1500),
                    "status" to "OPEN",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminMarkEnquiryResponded(enquiryId: String): Result<Unit> {
        val path = "enquiries/$enquiryId"
        return try {
            requireUserId()
            db.collection("enquiries").document(enquiryId)
                .update(
                    mapOf(
                        "status" to "RESPONDED",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }
}
