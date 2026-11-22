package com.example.data

import com.example.model.AgencyService
import com.example.model.AppLanguage
import com.example.model.PortfolioItem

data class WorkflowStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleTe: String,
    val detailEn: String,
    val detailTe: String
)

data class LegalTermItem(
    val titleEn: String,
    val titleTe: String,
    val bodyEn: String,
    val bodyTe: String
)

object BilingualContent {
    const val BRAND_NAME = "ALPHA MEDIA"
    const val TAGLINE = "CREATE • EDIT • INSPIRE"
    const val SLOGAN_EN = "Your Business. Our Creative Vision."
    const val SLOGAN_TE = "మీ వ్యాపారం… మా సృజనాత్మక ప్రచారం!"
    const val STUDIO_LOCATION_EN = "Pithapuram, Andhra Pradesh, India"
    const val STUDIO_LOCATION_TE = "పిఠాపురం, ఆంధ్రప్రదేశ్, భారతదేశం"

    // Contact placeholders until agency updates final numbers/handles
    const val PLACEHOLDER_WHATSAPP_NUMBER = "+919000000000" // Placeholder WhatsApp number
    const val PLACEHOLDER_PHONE_DISPLAY = "+91 90000 00000 (Studio Placeholder)"
    const val PLACEHOLDER_EMAIL = "contact@alphamedia.studio"
    const val PLACEHOLDER_INSTAGRAM = "@alphamedia.pithapuram"
    const val PLACEHOLDER_YOUTUBE = "@AlphaMediaStudioAP"

    val AD_TYPES = listOf(
        "AI Video Advertisement",
        "AI Image Advertisement",
        "AI Poster / Banner",
        "AI Animated Character Advertisement",
        "AI Voiceover / Dubbing",
        "Social Media Reel / Short"
    )

    val AD_TYPES_TE = mapOf(
        "AI Video Advertisement" to "AI వీడియో ప్రకటన",
        "AI Image Advertisement" to "AI చిత్ర ప్రకటన",
        "AI Poster / Banner" to "AI పోస్టర్ / బ్యానర్",
        "AI Animated Character Advertisement" to "AI యానిమేటెడ్ క్యారెక్టర్ ప్రకటన",
        "AI Voiceover / Dubbing" to "AI వాయిస్‌ఓవర్ / డబ్బింగ్",
        "Social Media Reel / Short" to "సోషల్ మీడియా రీల్ / షార్ట్"
    )

    val AD_LANGUAGES = listOf("Telugu", "English", "Hindi", "Other")
    val AD_LANGUAGES_TE = mapOf(
        "Telugu" to "తెలుగు",
        "English" to "ఇంగ్లీష్",
        "Hindi" to "హిందీ",
        "Other" to "ఇతర భాష"
    )

    val VIDEO_DURATIONS = listOf("15 seconds", "30 seconds", "45 seconds", "60 seconds", "90+ seconds", "Static Image / Poster")
    val BUDGET_RANGES = listOf("₹2,500 - ₹5,000", "₹5,000 - ₹10,000", "₹10,000 - ₹25,000", "₹25,000+ Custom Studio")

    val PORTFOLIO_CATEGORIES = listOf(
        "All",
        "Product Advertisements",
        "Dairy Advertisements",
        "Restaurant Advertisements",
        "Clothing Store Advertisements",
        "Business Promotional Videos",
        "Animated AI Advertisements"
    )

    val PORTFOLIO_CATEGORIES_TE = mapOf(
        "All" to "అన్నీ",
        "Product Advertisements" to "ఉత్పత్తి ప్రకటనలు",
        "Dairy Advertisements" to "డైరీ ప్రకటనలు",
        "Restaurant Advertisements" to "రెస్టారెంట్ ప్రకటనలు",
        "Clothing Store Advertisements" to "వస్త్ర దుకాణ ప్రకటనలు",
        "Business Promotional Videos" to "వ్యాపార ప్రచార వీడియోలు",
        "Animated AI Advertisements" to "యానిమేటెడ్ AI ప్రకటనలు"
    )

    fun t(lang: AppLanguage, en: String, te: String): String =
        if (lang == AppLanguage.TELUGU) te else en

    val defaultServices: List<AgencyService> = listOf(
        AgencyService(
            id = "srv_1",
            orderIndex = 1,
            titleEn = "AI Video Advertisement Production",
            titleTe = "AI వీడియో ప్రకటనల నిర్మాణం",
            descriptionEn = "Cinematic commercial video ads crafted with AI visuals, dynamic transitions, and crisp brand storytelling.",
            descriptionTe = "ఆధునిక AI దృశ్యాలు, ఆకర్షణీయమైన ట్రాన్సిషన్లు మరియు బ్రాండ్ కథనంతో కూడిన వాణిజ్య వీడియో ప్రకటనలు.",
            deliverablesEn = "1080p HD Video • Script • Storyboard",
            deliverablesTe = "1080p HD వీడియో • స్క్రిప్ట్ • స్టోరీబోర్డ్",
            adTypeKey = "AI Video Advertisement"
        ),
        AgencyService(
            id = "srv_2",
            orderIndex = 2,
            titleEn = "AI Product Image Generation",
            titleTe = "AI ఉత్పత్తి చిత్రాల రూపకల్పన",
            descriptionEn = "Studio-quality product visuals in rich lighting and custom backdrops for e-commerce, dairy, and retail brands.",
            descriptionTe = "మీ ఉత్పత్తులకు ఆకర్షణీయమైన లైటింగ్ మరియు నేపథ్యాలతో స్టూడియో స్థాయి చిత్రాల రూపకల్పన.",
            deliverablesEn = "High-Res Pack • Multiple Angles",
            deliverablesTe = "హై-రిజల్యూషన్ చిత్రాలు • విభిన్న కోణాలు",
            adTypeKey = "AI Image Advertisement"
        ),
        AgencyService(
            id = "srv_3",
            orderIndex = 3,
            titleEn = "AI Poster and Banner Design",
            titleTe = "AI పోస్టర్ మరియు బ్యానర్ డిజైన్",
            descriptionEn = "Festive offers, store launches, and digital hoardings designed in Telugu & English with sharp typography.",
            descriptionTe = "పండుగ ఆఫర్లు, దుకాణ ప్రారంభోత్సవాలు మరియు డిజిటల్ బ్యానర్ల కోసం తెలుగు & ఇంగ్లీష్ పోస్టర్లు.",
            deliverablesEn = "Print & Social Formats • Bilingual Copy",
            deliverablesTe = "ప్రింట్ & సోషల్ మీడియా ఫార్మాట్లు",
            adTypeKey = "AI Poster / Banner"
        ),
        AgencyService(
            id = "srv_4",
            orderIndex = 4,
            titleEn = "AI Animated Character Advertisements",
            titleTe = "AI యానిమేటెడ్ క్యారెక్టర్ ప్రకటనలు",
            descriptionEn = "Memorable mascots and stylized animated spokespersons that explain your product clearly to local audiences.",
            descriptionTe = "మీ ఉత్పత్తి వివరాలను స్థానిక వినియోగదారులకు సులభంగా వివరించే ఆకర్షణీయమైన యానిమేటెడ్ పాత్రలు.",
            deliverablesEn = "Custom Mascot • Lip-Synced Video",
            deliverablesTe = "ప్రత్యేక పాత్ర • వాయిస్ సింక్ వీడియో",
            adTypeKey = "AI Animated Character Advertisement"
        ),
        AgencyService(
            id = "srv_5",
            orderIndex = 5,
            titleEn = "Telugu AI Voice-over and Dubbing",
            titleTe = "తెలుగు AI వాయిస్-ఓవర్ మరియు డబ్బింగ్",
            descriptionEn = "Natural-sounding Telugu and English narration tailored for local commercials, explainer reels, and festive promos.",
            descriptionTe = "స్థానిక వ్యాపార ప్రకటనలు మరియు రీల్స్ కోసం స్పష్టమైన తెలుగు మరియు ఇంగ్లీష్ వాయిస్-ఓవర్ సేవలు.",
            deliverablesEn = "Studio Audio Mix • Bilingual Script",
            deliverablesTe = "స్టూడియో ఆడియో మిక్స్ • ద్విభాషా స్క్రిప్ట్",
            adTypeKey = "AI Voiceover / Dubbing"
        ),
        AgencyService(
            id = "srv_6",
            orderIndex = 6,
            titleEn = "Business Promotional Videos",
            titleTe = "వ్యాపార ప్రచార వీడియోలు",
            descriptionEn = "Complete overview videos for local shops, restaurants, clinics, showrooms, and emerging startups.",
            descriptionTe = "స్థానిక దుకాణాలు, రెస్టారెంట్లు, షోరూమ్‌లు మరియు స్టార్టప్‌ల కోసం పూర్తిస్థాయి ప్రచార వీడియోలు.",
            deliverablesEn = "30-60s Promo • Music & Titles",
            deliverablesTe = "30-60 సెకన్ల ప్రోమో • సంగీతం & టైటిల్స్",
            adTypeKey = "AI Video Advertisement"
        ),
        AgencyService(
            id = "srv_7",
            orderIndex = 7,
            titleEn = "Instagram Reels and YouTube Shorts",
            titleTe = "ఇన్‌స్టాగ్రామ్ రీల్స్ మరియు యూట్యూబ్ షార్ట్స్",
            descriptionEn = "Vertical 9:16 high-retention short videos structured with strong visual hooks and clear WhatsApp calls to action.",
            descriptionTe = "వెర్టికల్ 9:16 ఫార్మాట్‌లో తక్షణ ఆకర్షణ మరియు స్పష్టమైన వాట్సాప్ కాల్-టు-యాక్షన్‌తో కూడిన షార్ట్ వీడియోలు.",
            deliverablesEn = "9:16 Vertical MP4 • Hook Captions",
            deliverablesTe = "9:16 వెర్టికల్ MP4 • క్యాప్షన్లు",
            adTypeKey = "Social Media Reel / Short"
        ),
        AgencyService(
            id = "srv_8",
            orderIndex = 8,
            titleEn = "Social Media Creative Content",
            titleTe = "సోషల్ మీడియా క్రియేటివ్ కంటెంట్",
            descriptionEn = "Consistent weekly festival creatives, carousel banners, and product spotlight packs for small businesses.",
            descriptionTe = "చిన్న వ్యాపారాల కోసం వారపు పండుగ పోస్టులు, కరోసెల్ బ్యానర్లు మరియు ఉత్పత్తి స్పాట్‌లైట్ ప్యాక్‌లు.",
            deliverablesEn = "Monthly Creative Pack • Square & Story",
            deliverablesTe = "నెలవారీ క్రియేటివ్ ప్యాక్ • స్క్వేర్ & స్టోరీ",
            adTypeKey = "AI Image Advertisement"
        ),
        AgencyService(
            id = "srv_9",
            orderIndex = 9,
            titleEn = "Brand Introduction Videos",
            titleTe = "బ్రాండ్ పరిచయ వీడియోలు",
            descriptionEn = "Signature launch films introducing your brand mission, heritage, and product promise with cinematic polish.",
            descriptionTe = "మీ బ్రాండ్ లక్ష్యం, నాణ్యత మరియు ప్రత్యేకతను పరిచయం చేసే సినిమాటిక్ లాంచ్ వీడియోలు.",
            deliverablesEn = "Signature Brand Film • Logo Reveal",
            deliverablesTe = "బ్రాండ్ ఫిల్మ్ • లోగో రివీల్",
            adTypeKey = "AI Video Advertisement"
        ),
        AgencyService(
            id = "srv_10",
            orderIndex = 10,
            titleEn = "Video Editing and Post Production",
            titleTe = "వీడియో ఎడిటింగ్ మరియు పోస్ట్ ప్రొడక్షన్",
            descriptionEn = "Color grading, sound design, bilingual Telugu/English motion titles, and pacing polish for your raw footage.",
            descriptionTe = "మీ వీడియోలకు కలర్ గ్రేడింగ్, సౌండ్ డిజైన్ మరియు తెలుగు/ఇంగ్లీష్ మోషన్ టైటిల్స్‌తో పోస్ట్ ప్రొడక్షన్.",
            deliverablesEn = "Color Grade • SFX • Bilingual Titles",
            deliverablesTe = "కలర్ గ్రేడ్ • సౌండ్ ఎఫెక్ట్స్ • టైటిల్స్",
            adTypeKey = "AI Video Advertisement"
        )
    )

    val defaultPortfolioItems: List<PortfolioItem> = listOf(
        PortfolioItem(
            id = "demo_dairy",
            titleEn = "Pure Morning Farm Dairy Concept",
            titleTe = "స్వచ్ఛమైన ఫామ్ డైరీ కాన్సెప్ట్",
            category = "Dairy Advertisements",
            descriptionEn = "A 30-second AI concept advertisement illustrating farm-fresh milk delivery, golden morning light, and Telugu family narration.",
            descriptionTe = "ఉదయపు స్వచ్ఛమైన పాల పంపిణీ మరియు తెలుగు వాయిస్‌ఓవర్‌తో రూపొందించిన 30 సెకన్ల AI కాన్సెప్ట్ ప్రకటన.",
            durationLabel = "30s Concept Reel",
            visualKey = "dairy",
            storyboardSteps = listOf(
                "00-05s: Sunrise village pastures & traditional brass milk vessel macro",
                "05-15s: High-speed crystal milk splash with golden purity highlights",
                "15-24s: Telugu voiceover highlighting hygiene & daily doorstep delivery",
                "24-30s: Brand crest reveal with WhatsApp order call-to-action"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration sample only. Not an actual client campaign.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన నమూనా మాత్రమే.",
            isBuiltIn = true
        ),
        PortfolioItem(
            id = "demo_restaurant",
            titleEn = "Royal Spice Culinary Showcase Concept",
            titleTe = "రాయల్ స్పైస్ రెస్టారెంట్ కాన్సెప్ట్",
            category = "Restaurant Advertisements",
            descriptionEn = "Sensory AI food film concept featuring rising saffron steam, hammered brass plating, and weekend family dining appeal.",
            descriptionTe = "వేడివేడి బిర్యానీ ఆవిరి మరియు కుటుంబ భోజన ఆహ్వానంతో రూపొందించిన AI ఫుడ్ వీడియో కాన్సెప్ట్.",
            durationLabel = "25s Concept Short",
            visualKey = "restaurant",
            storyboardSteps = listOf(
                "00-06s: Dramatic macro spice roast & golden oil shimmer",
                "06-16s: Steaming royal biryani served in hammered brass vessel",
                "16-25s: Bilingual Telugu/English dining offer & location card"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration sample only. No fabricated business results.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
            isBuiltIn = true
        ),
        PortfolioItem(
            id = "demo_clothing",
            titleEn = "Festive Silk & Couture Boutique Concept",
            titleTe = "పట్టు వస్త్ర శ్రేణి బోటిక్ కాన్సెప్ట్",
            category = "Clothing Store Advertisements",
            descriptionEn = "Luxury royal-navy & gold bridal silk drape visualization designed for festival season clothing store promotions.",
            descriptionTe = "పండుగ సీజన్ వస్త్ర దుకాణ ప్రచారం కోసం రాయల్ నేవీ మరియు గోల్డ్ స్టేజ్‌పై AI సిల్క్ చీరల కాన్సెప్ట్.",
            durationLabel = "30s Concept Film",
            visualKey = "clothing",
            storyboardSteps = listOf(
                "00-08s: Golden silk zari weave close-up under studio spotlight",
                "08-20s: Flowing couture drape reveal in Royal Navy boutique setting",
                "20-30s: Festive collection title card with store address & timing"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration showcase only.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
            isBuiltIn = true
        ),
        PortfolioItem(
            id = "demo_product",
            titleEn = "Artisanal Herbal Oil Product Concept",
            titleTe = "హెర్బల్ ఉత్పత్తి లాంచ్ కాన్సెప్ట్",
            category = "Product Advertisements",
            descriptionEn = "Clean 3D-inspired AI product commercial concept highlighting botanical ingredients and premium glass packaging.",
            descriptionTe = "సహజ మూలికా పదార్థాలు మరియు ప్రీమియం ప్యాకేజింగ్‌ను చూపే AI ఉత్పత్తి ప్రకటన కాన్సెప్ట్.",
            durationLabel = "20s Concept Ad",
            visualKey = "product",
            storyboardSteps = listOf(
                "00-06s: Floating botanical leaves & golden droplets in dark studio",
                "06-14s: Sleek amber bottle rotation with crisp label lockup",
                "14-20s: Usage benefit summary & pack sizes display"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration showcase only.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
            isBuiltIn = true
        ),
        PortfolioItem(
            id = "demo_business",
            titleEn = "Local Startup Brand Intro Concept",
            titleTe = "స్థానిక స్టార్టప్ బ్రాండ్ పరిచయ కాన్సెప్ట్",
            category = "Business Promotional Videos",
            descriptionEn = "Modern corporate & local business introduction concept combining bilingual motion titles with clean brand storytelling.",
            descriptionTe = "స్థానిక వ్యాపారాల విలువలు మరియు సేవలను వివరించే ద్విభాషా బ్రాండ్ పరిచయ వీడియో కాన్సెప్ట్.",
            durationLabel = "45s Brand Intro",
            visualKey = "business",
            storyboardSteps = listOf(
                "00-12s: Brand problem statement & local community context",
                "12-32s: Service pillars & transparent process walkthrough",
                "32-45s: Contact details & Pithapuram / AP regional badge"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration showcase only.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
            isBuiltIn = true
        ),
        PortfolioItem(
            id = "demo_animated",
            titleEn = "Telugu Mascot Explainer Concept",
            titleTe = "తెలుగు యానిమేటెడ్ మస్కట్ కాన్సెప్ట్",
            category = "Animated AI Advertisements",
            descriptionEn = "Friendly stylized 3D animated character concept explaining digital store services in warm conversational Telugu.",
            descriptionTe = "సులభమైన తెలుగులో సేవలను వివరించే స్నేహపూర్వక యానిమేటెడ్ పాత్ర AI ప్రకటన కాన్సెప్ట్.",
            durationLabel = "30s Animated Concept",
            visualKey = "animated",
            storyboardSteps = listOf(
                "00-08s: Stylized character waves and welcomes viewers in Telugu",
                "08-22s: Interactive animated icons illustrate key product features",
                "22-30s: Character points to WhatsApp enquiry button"
            ),
            conceptNoticeEn = "AI Concept Advertisement — Demonstration showcase only.",
            conceptNoticeTe = "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
            isBuiltIn = true
        )
    )

    val workflowSteps: List<WorkflowStep> = listOf(
        WorkflowStep(1, "Client submits booking", "క్లయింట్ బుకింగ్ సమర్పిస్తారు", "Submit project details, media references, and accept production terms.", "ప్రాజెక్ట్ వివరాలు, రిఫరెన్స్ ఫొటోలు మరియు నిర్మాణ నిబంధనలను సమర్పించండి."),
        WorkflowStep(2, "Agency reviews requirements", "ఏజెన్సీ అవసరాలను పరిశీలిస్తుంది", "ALPHA MEDIA reviews brand goals, language, and technical scope.", "ALPHA MEDIA మీ బ్రాండ్ లక్ష్యాలు, భాష మరియు పరిధిని పరిశీలిస్తుంది."),
        WorkflowStep(3, "Discuss scope, price & delivery", "ధర మరియు డెలివరీ తేదీ చర్చ", "Confirm deliverables, agreed pricing, advance terms, and schedule.", "డెలివరబుల్స్, ధర, అడ్వాన్స్ వివరాలు మరియు గడువును ఖరారు చేస్తాము."),
        WorkflowStep(4, "Prepare script & storyboard", "స్క్రిప్ట్ మరియు స్టోరీబోర్డ్ తయారీ", "Craft bilingual Telugu/English script, voice cues, and visual plan.", "తెలుగు/ఇంగ్లీష్ స్క్రిప్ట్, వాయిస్ సూచనలు మరియు స్టోరీబోర్డ్ సిద్ధం చేస్తాము."),
        WorkflowStep(5, "Obtain client approval", "క్లయింట్ స్క్రిప్ట్ ఆమోదం", "Production starts only after you approve the script and brief.", "మీరు స్క్రిప్ట్ మరియు బ్రీఫ్ ఆమోదించిన తర్వాతే నిర్మాణం ప్రారంభమవుతుంది."),
        WorkflowStep(6, "Produce AI advertisement", "AI ప్రకటన నిర్మాణం", "Generate AI visuals, voiceover, motion graphics, and studio edits.", "AI విజువల్స్, వాయిస్-ఓవర్, మోషన్ గ్రాఫిక్స్ మరియు ఎడిటింగ్ నిర్వహిస్తాము."),
        WorkflowStep(7, "Send preview", "ప్రివ్యూ పంపబడుతుంది", "Review watermarked/draft preview for accuracy and pacing.", "నాణ్యత మరియు వివరాల పరిశీలన కోసం ప్రివ్యూను మీకు పంపుతాము."),
        WorkflowStep(8, "Obtain final approval", "తుది ఆమోదం పొందడం", "Client confirms business facts, product claims, and final cut.", "వ్యాపార వివరాలు, ధరలు మరియు తుది వీడియోను క్లయింట్ ధృవీకరిస్తారు."),
        WorkflowStep(9, "Deliver final advertisement", "తుది ప్రకటన డెలివరీ", "Receive final export-ready files upon payment completion.", "చెల్లింపు పూర్తయిన తర్వాత తుది ఫైళ్లను అందజేస్తాము."),
        WorkflowStep(10, "Separate publishing permission", "వేరుగా ప్రచురణ అనుమతి", "Optional written permission before any agency channel posting.", "ఏజెన్సీ ఛానెళ్లలో ప్రచురించే ముందు తప్పనిసరిగా వేరు అనుమతి తీసుకుంటాము.")
    )

    val productionAgreementClauses: List<Pair<String, String>> = listOf(
        "1. The client confirms that the business, product and advertising information supplied is accurate." to
            "1. తాము అందించిన వ్యాపారం, ఉత్పత్తి మరియు ప్రకటన వివరాలు ఖచ్చితమైనవని క్లయింట్ ధృవీకరిస్తున్నారు.",
        "2. The client confirms that they have permission to use all logos, photographs, music, videos, trademarks and other materials they provide." to
            "2. తాము అందించిన లోగోలు, ఫొటోలు, సంగీతం, వీడియోలు మరియు ట్రేడ్‌మార్క్‌లను ఉపయోగించడానికి తమకు పూర్తి అనుమతి ఉందని క్లయింట్ ధృవీకరిస్తున్నారు.",
        "3. ALPHA MEDIA will produce the agreed AI-generated advertisement according to the approved project brief." to
            "3. ఆమోదించబడిన ప్రాజెక్ట్ బ్రీఫ్ ప్రకారం ALPHA MEDIA అంగీకరించిన AI ప్రకటనను నిర్మిస్తుంది.",
        "4. The client must review and approve the script, design or preview before final production or publication, as applicable." to
            "4. తుది నిర్మాణం లేదా ప్రచురణకు ముందు క్లయింట్ స్క్రిప్ట్, డిజైన్ లేదా ప్రివ్యూను పరిశీలించి ఆమోదించాలి.",
        "5. Changes outside the agreed project scope may require additional charges, subject to client approval." to
            "5. అంగీకరించిన ప్రాజెక్ట్ పరిధికి మించిన మార్పులకు క్లయింట్ ఆమోదంతో అదనపు ఛార్జీలు వర్తించవచ్చు.",
        "6. Delivery dates depend on project requirements, client feedback and approval." to
            "6. డెలివరీ తేదీలు ప్రాజెక్ట్ అవసరాలు, క్లయింట్ సూచనలు మరియు ఆమోదంపై ఆధారపడి ఉంటాయి.",
        "7. AI-generated content may contain limitations or inaccuracies. The client must verify product claims, prices, contact details and other factual information before publication." to
            "7. AI ద్వారా రూపొందించిన కంటెంట్‌లో పరిమితులు లేదా తప్పులు ఉండవచ్చు. ప్రచురణకు ముందు ఉత్పత్తి వివరాలు, ధరలు మరియు ఫోన్ నంబర్లను క్లయింట్ తప్పనిసరిగా సరిచూసుకోవాలి.",
        "8. ALPHA MEDIA does not guarantee sales, profits, views, followers or business growth." to
            "8. అమ్మకాలు, లాభాలు, వ్యూస్, ఫాలోవర్లు లేదా వ్యాపార వృద్ధికి ALPHA MEDIA ఎటువంటి హామీ ఇవ్వదు.",
        "9. Payment, cancellation, refund and revision terms must be displayed clearly and agreed before work begins." to
            "9. పని ప్రారంభించే ముందు చెల్లింపు, రద్దు, రీఫండ్ మరియు మార్పుల నిబంధనలను స్పష్టంగా అంగీకరించాలి.",
        "10. The final ownership and permitted usage of the deliverables must be specified in the individual project agreement." to
            "10. తుది ఫైళ్ల యాజమాన్యం మరియు అనుమతించబడిన వినియోగ నిబంధనలు వ్యక్తిగత ప్రాజెక్ట్ ఒప్పందంలో పేర్కొనబడతాయి."
    )

    val legalTermsList: List<LegalTermItem> = listOf(
        LegalTermItem(
            "Client Asset Ownership & Permissions",
            "క్లయింట్ ఆస్తుల యాజమాన్యం & అనుమతులు",
            "Clients retain ownership of supplied logos, trademarks, and product photos and warrant they hold legal rights for commercial use.",
            "క్లయింట్ అందించే లోగోలు, ట్రేడ్‌మార్క్‌లు మరియు ఉత్పత్తి చిత్రాల యాజమాన్యం క్లయింట్‌దే. వాటి వాణిజ్య వినియోగ హక్కులు తమకు ఉన్నాయని క్లయింట్ నిర్ధారించాలి."
        ),
        LegalTermItem(
            "AI-Generated Content Disclosure",
            "AI కంటెంట్ ప్రకటన",
            "Deliverables utilize generative AI visuals, motion, or synthetic voiceovers. Real faces or voices require explicit consent.",
            "ప్రకటనలలో AI విజువల్స్ లేదా AI వాయిస్‌లు ఉపయోగించబడవచ్చు. నిజమైన వ్యక్తుల ముఖం లేదా గొంతు ఉపయోగించడానికి స్పష్టమైన అనుమతి తప్పనిసరి."
        ),
        LegalTermItem(
            "Client Approval Before Publication",
            "ప్రచురణకు ముందు క్లయింట్ ఆమోదం",
            "No advertisement is considered final or publish-ready until the client reviews facts, prices, and approves the preview.",
            "క్లయింట్ ధరలు, సంప్రదింపు వివరాలు మరియు ప్రివ్యూను ఆమోదించే వరకు ఏ ప్రకటనా ప్రచురణకు సిద్ధమైనట్లు పరిగణించబడదు."
        ),
        LegalTermItem(
            "Separate Written Publishing Permission",
            "వేరుగా వ్రాతపూర్వక ప్రచురణ అనుమతి",
            "Silence or form submission never implies publishing consent. Agency social or portfolio showcase requires explicit opt-in.",
            "బుకింగ్ సమర్పించడం వల్ల ఏజెన్సీ ప్రచురణ అనుమతి లభించదు. పోర్ట్‌ఫోలియో లేదా సోషల్ మీడియాలో ప్రదర్శించడానికి వేరుగా అనుమతి అవసరం."
        ),
        LegalTermItem(
            "Project Usage & Licensing Terms",
            "వినియోగ మరియు లైసెన్సింగ్ నిబంధనలు",
            "Permitted ad channels, territory, and deliverable formats are governed by the project's agreed scope and payment completion.",
            "ప్రకటనల వినియోగ మాధ్యమాలు మరియు ఫార్మాట్లు వ్యక్తిగత ప్రాజెక్ట్ ఒప్పందం మరియు పూర్తి చెల్లింపుపై ఆధారపడి ఉంటాయి."
        ),
        LegalTermItem(
            "Privacy Policy & Data Handling",
            "గోప్యతా విధానం & డేటా భద్రత",
            "Client bookings, phone numbers, and agreements are stored privately per user in Firebase Firestore. Clients may request corrections or withdraw optional publishing consent anytime.",
            "క్లయింట్ వివరాలు మరియు ఒప్పందాలు Firebase Firestoreలో సురక్షితంగా నిల్వ చేయబడతాయి. డేటా సవరణలు లేదా ప్రచురణ అనుమతి ఉపసంహరణను ఎప్పుడైనా కోరవచ్చు."
        ),
        LegalTermItem(
            "Cancellation, Refund & Revision Policy",
            "రద్దు, రీఫండ్ & సవరణల విధానం",
            "Revisions within agreed script scope are included prior to final render. Advance payments cover pre-production scripting and compute costs once work starts.",
            "తుది రెండర్‌కు ముందు అంగీకరించిన పరిధిలో సవరణలు అందించబడతాయి. పని ప్రారంభమైన తర్వాత స్క్రిప్టింగ్ మరియు AI ఉత్పత్తి ఖర్చులకు అడ్వాన్స్ వర్తిస్తుంది."
        ),
        LegalTermItem(
            "No Guaranteed Sales or Profits",
            "అమ్మకాలు లేదా లాభాలపై హామీ లేదు",
            "ALPHA MEDIA provides creative production services only and does not guarantee sales, views, leads, followers, or revenue.",
            "ALPHA MEDIA సృజనాత్మక ప్రకటనల సేవలను మాత్రమే అందిస్తుంది; అమ్మకాలు, లాభాలు, వ్యూస్ లేదా వ్యాపార వృద్ధికి హామీ ఇవ్వదు."
        ),
        LegalTermItem(
            "Legal Review & Digital Signature Notice",
            "న్యాయ సమీక్ష & డిజిటల్ సంతక గమనిక",
            "Digital consent records in this app support project workflow tracking. Formal commercial contracts and legally binding e-signatures require appropriate legal review and/or an external e-signature service.",
            "ఈ యాప్‌లోని డిజిటల్ ఒప్పందం ప్రాజెక్ట్ ట్రాకింగ్ కోసం. అధికారిక చట్టపరమైన ఒప్పందాలకు తగిన న్యాయ సమీక్ష మరియు/లేదా బాహ్య ఇ-సిగ్నేచర్ సేవ అవసరం."
        )
    )
}
