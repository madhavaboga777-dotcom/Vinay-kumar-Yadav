package com.example

import com.example.data.BilingualContent
import com.example.model.AppLanguage
import com.example.model.PaymentStatus
import com.example.model.ProjectStatus
import com.example.model.PublishingStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `bilingual content contains 10 services, 10 workflow steps, and 10 agreement clauses`() {
        assertEquals(10, BilingualContent.defaultServices.size)
        assertEquals(10, BilingualContent.workflowSteps.size)
        assertEquals(10, BilingualContent.productionAgreementClauses.size)
        assertEquals(6, BilingualContent.defaultPortfolioItems.size)
        assertEquals("ALPHA MEDIA", BilingualContent.BRAND_NAME)
        assertEquals(
            "మీ వ్యాపారం… మా సృజనాత్మక ప్రచారం!",
            BilingualContent.t(AppLanguage.TELUGU, BilingualContent.SLOGAN_EN, BilingualContent.SLOGAN_TE)
        )
    }

    @Test
    fun `portfolio defaults are explicitly marked as AI concept advertisements`() {
        assertTrue(
            BilingualContent.defaultPortfolioItems.all {
                it.conceptNoticeEn.contains("AI Concept", ignoreCase = true) &&
                    it.conceptNoticeTe.contains("AI కాన్సెప్ట్")
            }
        )
    }

    @Test
    fun `status enums safely map keys and default cleanly`() {
        assertEquals(ProjectStatus.DELIVERED, ProjectStatus.fromKey("DELIVERED"))
        assertEquals(ProjectStatus.NEW, ProjectStatus.fromKey("UNKNOWN_KEY"))
        assertEquals(PublishingStatus.NOT_AUTHORIZED, PublishingStatus.fromKey(null))
        assertEquals(PublishingStatus.REVOKED, PublishingStatus.fromKey("REVOKED"))
        assertEquals(PaymentStatus.ADVANCE_PAID, PaymentStatus.fromKey("ADVANCE_PAID"))
        assertFalse(BilingualContent.AD_TYPES.isEmpty())
    }

    @Test
    fun `mobile number E164 normalization validates Indian and international numbers cleanly`() {
        assertEquals("+919876543210", com.example.auth.AlphaAuthHelper.normalizePhoneE164("+91", "9876543210"))
        assertEquals("+919876543210", com.example.auth.AlphaAuthHelper.normalizePhoneE164("91", "098765 43210"))
        assertEquals("+919876543210", com.example.auth.AlphaAuthHelper.normalizePhoneE164("+91", "+91 98765-43210"))
        assertEquals("+14155552671", com.example.auth.AlphaAuthHelper.normalizePhoneE164("+1", "4155552671"))
        assertEquals(null, com.example.auth.AlphaAuthHelper.normalizePhoneE164("+91", "12345"))
    }
}

