package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.BilingualContent
import com.example.model.BookingAgreement
import java.io.File
import java.io.FileOutputStream

object AgreementPdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 40f
    private const val CONTENT_WIDTH = (PAGE_WIDTH - 80)

    fun generateAgreementPdf(context: Context, booking: BookingAgreement): Result<Uri> {
        return try {
            val pdfDocument = PdfDocument()
            var pageNumber = 1
            var page = pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            var canvas = page.canvas
            var y = drawHeader(context, canvas, booking)

            val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0B1633")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#1E293B")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#D4AF37")
                strokeWidth = 1.5f
            }

            fun ensureSpace(neededHeight: Float) {
                if (y + neededHeight > PAGE_HEIGHT - MARGIN) {
                    drawFooter(canvas, pageNumber, booking.agreementReferenceNumber)
                    pdfDocument.finishPage(page)
                    pageNumber += 1
                    page = pdfDocument.startPage(
                        PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    )
                    canvas = page.canvas
                    y = MARGIN + 10f
                }
            }

            fun drawWrapped(text: String, paint: TextPaint, extraBottom: Float = 6f) {
                val layout = StaticLayout.Builder.obtain(
                    text, 0, text.length, paint, CONTENT_WIDTH
                )
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(2f, 1.1f)
                    .build()
                ensureSpace(layout.height + extraBottom)
                canvas.save()
                canvas.translate(MARGIN, y)
                layout.draw(canvas)
                canvas.restore()
                y += layout.height + extraBottom
            }

            fun drawSectionTitle(title: String) {
                ensureSpace(28f)
                y += 6f
                canvas.drawText(title, MARGIN, y + 12f, sectionPaint)
                y += 17f
                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, accentPaint)
                y += 8f
            }

            // Section 1: Booking & Project Details
            drawSectionTitle("1. ADVERTISEMENT BOOKING & PROJECT DETAILS")
            val details = listOf(
                "Booking Reference: ${booking.referenceNumber}   |   Agreement Ref: ${booking.agreementReferenceNumber}",
                "Project Status: ${booking.projectStatus}   |   Payment Status: ${booking.paymentStatus}   |   Version: v${booking.agreementVersion}",
                "Client Name: ${booking.clientFullName}   |   Business: ${booking.businessName}",
                "Business Address: ${booking.businessAddress}",
                "WhatsApp: ${booking.whatsappNumber}   |   Email: ${booking.emailAddress.ifBlank { "N/A" }}",
                "Advertisement Title: ${booking.adTitle}   |   Product/Service: ${booking.productServiceName}",
                "Type: ${booking.adType}   |   Language: ${booking.adLanguage}   |   Duration: ${booking.videoDuration}",
                "Deliverables Count: ${booking.deliverablesCount}   |   Expected Delivery Date: ${booking.expectedDeliveryDate}",
                "Budget Range: ${booking.budgetRange}   |   Agreed Price: ${booking.agreedPrice.ifBlank { "Pending quote confirmation" }}",
                "Advance Payment Details: ${booking.advancePaymentDetails.ifBlank { "To be finalized prior to production" }}",
                "Reference Media Summary: ${booking.referenceMediaSummary.ifBlank { "None attached" }}",
                "Project Description: ${booking.projectDescription}",
                "Special Instructions: ${booking.specialInstructions.ifBlank { "None" }}"
            )
            details.forEach { drawWrapped(it, bodyPaint, 4f) }

            // Section 2: 10-Point Production Agreement
            drawSectionTitle("2. AI ADVERTISEMENT PRODUCTION AGREEMENT")
            BilingualContent.productionAgreementClauses.forEach { (enClause, _) ->
                drawWrapped(enClause, bodyPaint, 4f)
            }

            // Section 3: Publishing Rights & Disclosures
            drawSectionTitle("3. AI ADVERTISEMENT PUBLISHING RIGHTS & DISCLOSURES")
            val pubSummary = buildString {
                appendLine("Publishing Authorization Status: ${booking.publishingStatus}")
                appendLine("• Client Social Media Accounts: ${if (booking.publishClientSocial) "AUTHORIZED" else "No"}")
                appendLine("• ALPHA MEDIA Official Social Accounts: ${if (booking.publishAgencySocial) "AUTHORIZED" else "No"}")
                appendLine("• ALPHA MEDIA Portfolio & Demo Use: ${if (booking.publishAgencyPortfolio) "AUTHORIZED" else "No"}")
                appendLine("• Do Not Publish Publicly: ${if (booking.publishDoNotPublish) "YES (Strictly Private)" else "No"}")
                appendLine("AI Content Disclosure Accepted: ${if (booking.aiContentDisclosureAccepted) "YES" else "NO"}")
                appendLine("Real Person Face/Voice Permission Confirmed: ${if (booking.realPersonVoiceConsentAccepted) "YES" else "NO"}")
                append("Client Final Approval: ${if (booking.clientFinalApproved) "APPROVED" else "Pending Review"} | Factual Accuracy Confirmed: ${if (booking.clientApprovalAccuracy) "YES" else "Pending"}")
            }
            drawWrapped(pubSummary, bodyPaint, 6f)

            // Section 4: Digital Confirmation & Legal Review Notice
            drawSectionTitle("4. DIGITAL AGREEMENT CONFIRMATION & NOTICE")
            val signSummary = buildString {
                appendLine("Client Full Name: ${booking.clientFullName}")
                appendLine("Client Digital Signature: ${booking.clientSignature}")
                appendLine("Date of Agreement: ${booking.agreementDate}")
                appendLine("ALPHA MEDIA Representative: ${booking.representativeName}")
                appendLine("Production Agreement Accepted: ${if (booking.productionAgreementAccepted) "ACCEPTED" else "PENDING"}")
                append("Legal Notice: Digital consent records in this application support project workflow tracking. Formal commercial contracts and legally binding e-signatures require appropriate legal review and/or an external e-signature service.")
            }
            drawWrapped(signSummary, bodyPaint, 8f)

            drawFooter(canvas, pageNumber, booking.agreementReferenceNumber)
            pdfDocument.finishPage(page)

            val dir = File(context.cacheDir, "agreements").apply { mkdirs() }
            val safeRef = booking.agreementReferenceNumber.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            val file = File(dir, "AlphaMedia_${safeRef}_v${booking.agreementVersion}.pdf")
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun drawHeader(context: Context, canvas: Canvas, booking: BookingAgreement): Float {
        val headerBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0A1638")
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 106f, headerBg)

        val blueLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#007BFF")
            strokeWidth = 3f
        }
        val redLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E51923")
            strokeWidth = 3f
        }
        canvas.drawLine(0f, 106f, PAGE_WIDTH * 0.5f, 106f, blueLine)
        canvas.drawLine(PAGE_WIDTH * 0.5f, 106f, PAGE_WIDTH.toFloat(), 106f, redLine)

        var textStartX = MARGIN
        runCatching {
            val rawBitmap = android.graphics.BitmapFactory.decodeResource(
                context.resources,
                com.example.R.drawable.alpha_media_logo
            )
            if (rawBitmap != null) {
                val badgeBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                }
                val boxLeft = MARGIN
                val boxTop = 14f
                val boxWidth = 108f
                val boxHeight = 76f
                canvas.drawRoundRect(
                    android.graphics.RectF(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight),
                    8f,
                    8f,
                    badgeBg
                )
                val destRect = android.graphics.RectF(
                    boxLeft + 4f,
                    boxTop + 4f,
                    boxLeft + boxWidth - 4f,
                    boxTop + boxHeight - 4f
                )
                canvas.drawBitmap(rawBitmap, null, destRect, null)
                textStartX = boxLeft + boxWidth + 14f
            }
        }

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#56ACFF")
            textSize = 10f
        }
        val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 9f
        }

        canvas.drawText("ALPHA MEDIA  •  AI ADVERTISING STUDIO", textStartX, 36f, brandPaint)
        canvas.drawText("CREATE • EDIT • INSPIRE   |   Your Business. Our Creative Vision.", textStartX, 56f, subPaint)
        canvas.drawText(
            "Pithapuram, AP   |   Ref: ${booking.referenceNumber}   |   Agr: ${booking.agreementReferenceNumber}",
            textStartX,
            78f,
            metaPaint
        )
        return 122f
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int, agrRef: String) {
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#64748B")
            textSize = 8.5f
        }
        canvas.drawText(
            "ALPHA MEDIA — Confidential Client Booking & Agreement ($agrRef)   •   Page $pageNumber",
            MARGIN,
            PAGE_HEIGHT - 18f,
            footerPaint
        )
    }
}
