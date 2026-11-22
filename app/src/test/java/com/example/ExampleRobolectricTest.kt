package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.model.BookingAgreement
import com.example.util.AgreementPdfGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Alpha Media", appName)
    }

    @Test
    fun `bilingual content contains 10 services and 10 agreement clauses`() {
        assertEquals(10, BilingualContent.defaultServices.size)
        assertEquals(10, BilingualContent.productionAgreementClauses.size)
        assertEquals(10, BilingualContent.workflowSteps.size)
        assertEquals("ALPHA MEDIA", BilingualContent.BRAND_NAME)
        assertEquals(
            "మీ వ్యాపారం… మా సృజనాత్మక ప్రచారం!",
            BilingualContent.t(AppLanguage.TELUGU, BilingualContent.SLOGAN_EN, BilingualContent.SLOGAN_TE)
        )
    }

    @Test
    fun `agreement pdf generator creates non-empty file uri`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sample = BookingAgreement(
            referenceNumber = "AM-20261121-1001",
            agreementReferenceNumber = "AGR-20261121-1001",
            clientFullName = "Lakshmi Narayana",
            businessName = "Sri Krishna Dairy",
            businessAddress = "Main Road, Pithapuram",
            whatsappNumber = "+919000000000",
            adTitle = "Pure Milk Launch",
            productServiceName = "Fresh Milk",
            adType = "AI Video Advertisement",
            adLanguage = "Telugu",
            videoDuration = "30 seconds",
            deliverablesCount = 1L,
            budgetRange = "₹5,000 - ₹10,000",
            projectDescription = "Farm fresh dairy reel.",
            expectedDeliveryDate = "2026-11-30",
            productionAgreementAccepted = true,
            aiContentDisclosureAccepted = true,
            realPersonVoiceConsentAccepted = true,
            clientSignature = "Lakshmi Narayana",
            agreementDate = "2026-11-21"
        )
        val result = AgreementPdfGenerator.generateAgreementPdf(context, sample)
        assertTrue(result.isSuccess)
    }
}
