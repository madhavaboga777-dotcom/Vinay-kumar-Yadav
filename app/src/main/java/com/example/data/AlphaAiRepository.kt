package com.example.data

import android.util.Log
import com.example.model.GroundingSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

data class MarketResearchResult(
    val summary: String,
    val sources: List<GroundingSource>
)

data class ThinkingStreamChunk(
    val thoughts: String,
    val answer: String,
    val thoughtsTokenCount: Int? = null,
    val isFinal: Boolean = false
)

class AlphaAiRepository {
    companion object {
        private const val TAG = "AlphaAiRepo"
        private const val MODEL_SEARCH_GROUNDING = "gemini-4-hydrogen"
        private const val MODEL_HIGH_THINKING = "gemini-4-carbon"
        private const val AI_TIMEOUT_MS = 60_000L
    }

    // 1. Search-Grounded Market & Trend Research Model (gemini-4-hydrogen + Google Search)
    private val searchGroundedModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_SEARCH_GROUNDING,
            tools = listOf(Tool.googleSearch())
        )
    }

    // 2. High-Thinking Deep Creative Strategist Model (gemini-4-carbon + ThinkingLevel.HIGH, no maxOutputTokens)
    private val deepThinkingModel by lazy {
        val config = generationConfig {
            thinkingConfig = thinkingConfig {
                thinkingLevel = ThinkingLevel.HIGH
                includeThoughts = true
            }
            // Do NOT set maxOutputTokens when ThinkingLevel.HIGH is configured.
        }
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_HIGH_THINKING,
            generationConfig = config
        )
    }

    suspend fun researchAdTrendsWithGoogleSearch(
        businessType: String,
        regionOrLanguage: String,
        goal: String
    ): Result<MarketResearchResult> {
        return try {
            val prompt = """
                You are an AI Advertising Market Researcher for ALPHA MEDIA (Pithapuram, Andhra Pradesh, India).
                Use Google Search to gather current, real-world digital advertising, festival marketing, and social media reel trends relevant to:
                - Business / Industry: $businessType
                - Target Market / Language: $regionOrLanguage
                - Campaign Goal: $goal

                Provide:
                1. Current seasonal or regional marketing opportunities in India / Andhra Pradesh.
                2. Top-performing short-form ad hooks & visual formats for this category.
                3. Clear bilingual (English + Telugu) campaign angle suggestions.
                Do NOT invent fake metrics or fake client testimonials. Keep it actionable and concise.
            """.trimIndent()

            val response = withTimeout(AI_TIMEOUT_MS) {
                searchGroundedModel.generateContent(prompt)
            }

            val text = response.text?.trim().orEmpty()
            if (text.isBlank()) {
                return Result.failure(IllegalStateException("AI returned an empty market research response."))
            }

            val groundingChunks = response.candidates.firstOrNull()?.groundingMetadata?.groundingChunks
            val sources = groundingChunks?.mapNotNull { chunk ->
                val web = chunk.web
                val uri = web?.uri
                if (!uri.isNullOrBlank()) {
                    GroundingSource(
                        title = web.title?.takeIf { it.isNotBlank() } ?: uri,
                        url = uri
                    )
                } else {
                    null
                }
            }?.distinctBy { it.url } ?: emptyList()

            Result.success(MarketResearchResult(summary = text, sources = sources))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Search-grounded market research failed", e)
            Result.failure(e)
        }
    }

    fun streamDeepCreativeStrategy(
        businessName: String,
        productOrService: String,
        adType: String,
        adLanguage: String,
        duration: String,
        audienceNotes: String
    ): Flow<ThinkingStreamChunk> = flow {
        val prompt = """
            You are the Lead Creative Director & AI Strategist at ALPHA MEDIA (CREATE • EDIT • INSPIRE, Pithapuram, Andhra Pradesh).
            Develop a complete, production-ready AI advertisement blueprint for:
            - Business Name: $businessName
            - Product / Service: $productOrService
            - Advertisement Type: $adType
            - Primary Language: $adLanguage
            - Target Duration: $duration
            - Extra Context: $audienceNotes

            Output a structured creative package with:
            1. STRATEGIC HOOK & POSITIONING (Why this angle converts local customers)
            2. SCENE-BY-SCENE STORYBOARD (Timestamps, Visual AI Prompt direction, Camera motion)
            3. BILINGUAL SCRIPT & VOICEOVER (Provide crisp Telugu script in Telugu script + English script/subtitles)
            4. AI PRODUCTION & COMPLIANCE CHECKLIST (Disclosures, fact verification reminders, WhatsApp CTA)
        """.trimIndent()

        val thoughtsBuilder = StringBuilder()
        val answerBuilder = StringBuilder()
        var lastTokenCount: Int? = null

        withTimeout(AI_TIMEOUT_MS) {
            deepThinkingModel.generateContentStream(prompt).collect { chunk ->
                val chunkThought = chunk.thoughtSummary
                if (!chunkThought.isNullOrEmpty()) {
                    thoughtsBuilder.append(chunkThought)
                }
                val chunkText = chunk.text
                if (!chunkText.isNullOrEmpty()) {
                    answerBuilder.append(chunkText)
                }
                chunk.usageMetadata?.thoughtsTokenCount?.let { count ->
                    lastTokenCount = count
                }
                emit(
                    ThinkingStreamChunk(
                        thoughts = thoughtsBuilder.toString(),
                        answer = answerBuilder.toString(),
                        thoughtsTokenCount = lastTokenCount,
                        isFinal = false
                    )
                )
            }
        }

        if (answerBuilder.isBlank()) {
            throw IllegalStateException("Deep creative strategist returned an empty response.")
        }

        emit(
            ThinkingStreamChunk(
                thoughts = thoughtsBuilder.toString(),
                answer = answerBuilder.toString(),
                thoughtsTokenCount = lastTokenCount,
                isFinal = true
            )
        )
    }
}
