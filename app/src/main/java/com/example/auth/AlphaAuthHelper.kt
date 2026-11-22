package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "AlphaAuth"

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

object AlphaAuthHelper {

    /**
     * Normalizes a mobile number with country code (default +91) into E.164 format.
     * Returns null if the number fails basic length validation.
     */
    fun normalizePhoneE164(countryCode: String, rawMobile: String): String? {
        val trimmed = rawMobile.trim()
        if (trimmed.isEmpty()) return null

        val digitsOnly = trimmed.filter { it.isDigit() }
        val normalizedCode = countryCode.trim().let { cc ->
            val codeDigits = cc.filter { it.isDigit() }
            if (codeDigits.isEmpty()) "+91" else "+$codeDigits"
        }

        val e164 = if (trimmed.startsWith("+")) {
            "+$digitsOnly"
        } else if (normalizedCode == "+91" && digitsOnly.length == 12 && digitsOnly.startsWith("91")) {
            "+$digitsOnly"
        } else if (digitsOnly.length == 11 && digitsOnly.startsWith("0")) {
            "$normalizedCode${digitsOnly.drop(1)}"
        } else {
            "$normalizedCode$digitsOnly"
        }

        val totalDigits = e164.drop(1)
        return if (totalDigits.length in 10..15 && totalDigits.all { it.isDigit() }) {
            e164
        } else {
            null
        }
    }

    fun startPhoneVerification(
        activity: Activity,
        phoneE164: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        val builder = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneE164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }
        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<FirebaseUser> {
        return try {
            val result = Firebase.auth.signInWithCredential(credential).await()
            val user = result.user ?: throw IllegalStateException("Authentication succeeded without user profile.")
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Phone sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun verifyOtpCode(verificationId: String, smsCode: String): Result<FirebaseUser> {
        val cleaned = smsCode.trim()
        if (cleaned.length < 4) {
            return Result.failure(IllegalArgumentException("Please enter the 6-digit OTP sent to your mobile."))
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, cleaned)
        return signInWithPhoneCredential(credential)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = Firebase.auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw IllegalStateException("Authentication succeeded without user profile.")
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Email sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(
        displayName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = Firebase.auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw IllegalStateException("Account creation succeeded without user profile.")
            val cleanName = displayName.trim()
            if (cleanName.isNotEmpty()) {
                runCatching {
                    user.updateProfile(
                        userProfileChangeRequest { this.displayName = cleanName }
                    ).await()
                }
            }
            runCatching { user.sendEmailVerification().await() }
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Email registration failed", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            Firebase.auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            Result.failure(e)
        }
    }

    suspend fun sendVerificationEmailToCurrentUser(): Result<Unit> {
        return try {
            val user = Firebase.auth.currentUser
                ?: return Result.failure(IllegalStateException("No signed-in user found."))
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send verification email", e)
            Result.failure(e)
        }
    }

    suspend fun reloadCurrentUser(): Result<Boolean> {
        return try {
            val user = Firebase.auth.currentUser ?: return Result.success(false)
            user.reload().await()
            Result.success(user.isEmailVerified)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "User reload failed", e)
            Result.failure(e)
        }
    }

    fun signOutUser(
        scope: CoroutineScope,
        onSignOutComplete: () -> Unit = {}
    ) {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    Firebase.auth.signOut()
                } catch (e: Exception) {
                    Log.w(TAG, "Error during sign out: ${e.message}")
                } finally {
                    onSignOutComplete()
                }
            }
        }
    }

    fun formatAuthError(error: Throwable, isTelugu: Boolean): String {
        val code = (error as? FirebaseAuthException)?.errorCode.orEmpty()
        return when {
            error is FirebaseAuthInvalidUserException ->
                if (isTelugu) "ఈ ఖాతా కనుగొనబడలేదు. దయచేసి కొత్త ఖాతాను నమోదు చేయండి లేదా వివరాలను తనిఖీ చేయండి."
                else "No account found for these credentials. Please check your details or register first."

            error is FirebaseAuthInvalidCredentialsException || code == "ERROR_INVALID_VERIFICATION_CODE" ->
                if (isTelugu) "తప్పు పాస్‌వర్డ్, OTP లేదా ఇమెయిల్. దయచేసి మళ్లీ తనిఖీ చేయండి."
                else "Invalid credentials or OTP code. Please double-check and try again."

            error is FirebaseAuthUserCollisionException ->
                if (isTelugu) "ఈ ఇమెయిల్ ఇప్పటికే నమోదు చేయబడింది. దయచేసి సైన్ ఇన్ చేయండి."
                else "An account with this email already exists. Please sign in instead."

            error is FirebaseTooManyRequestsException ->
                if (isTelugu) "అధిక ప్రయత్నాల కారణంగా తాత్కాలికంగా బ్లాక్ చేయబడింది. కొద్దిసేపటి తర్వాత మళ్లీ ప్రయత్నించండి."
                else "Too many attempts right now. Please wait a short while and try again."

            code == "ERROR_OPERATION_NOT_ALLOWED" || code == "CONFIGURATION_NOT_FOUND" ->
                if (isTelugu) "ఈ లాగిన్ పద్ధతి Firebase కన్సోల్‌లో ప్రారంభించబడాలి. తక్షణ సహాయం కోసం WhatsApp లో సంప్రదించండి లేదా లాగిన్ లేకుండా బుకింగ్ చేయండి."
                else "This sign-in provider must be enabled in Firebase Console -> Authentication -> Sign-in method. You can also book without login or contact us on WhatsApp."

            error is FirebaseException && (error.message?.contains("app-check", ignoreCase = true) == true ||
                error.message?.contains("reCAPTCHA", ignoreCase = true) == true ||
                error.message?.contains("Play Integrity", ignoreCase = true) == true) ->
                if (isTelugu) "మొబైల్ ధృవీకరణకు Firebase ఫోన్ Auth సెటప్ అవసరం. మీరు లాగిన్ లేకుండా ప్రకటన బుక్ చేయవచ్చు లేదా WhatsApp లో సంప్రదించవచ్చు."
                else "Mobile OTP verification requires Firebase Phone Auth configuration in Firebase Console. You can still book an advertisement without login or reach us on WhatsApp."

            else -> error.localizedMessage ?: if (isTelugu) "లాగిన్ విఫలమైంది." else "Authentication failed."
        }
    }
}
