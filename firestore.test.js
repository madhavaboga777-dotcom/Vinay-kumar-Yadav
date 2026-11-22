const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_client_123";
const BOB_UID = "bob_client_456";
const ADMIN_UID = "admin_alpha_777";
const ADMIN_EMAIL = "madhavaboga777@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validBookingPayload(uid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    userEmail: "alice@example.com",
    referenceNumber: "AM-20261121-A1B2",
    agreementReferenceNumber: "AGR-20261121-A1B2",
    clientFullName: "Lakshmi Narayana",
    businessName: "Sri Krishna Dairy",
    businessAddress: "Main Road, Pithapuram, Andhra Pradesh",
    whatsappNumber: "+919876543210",
    emailAddress: "dairy@example.com",
    adTitle: "Pure Farm Milk Launch Reel",
    productServiceName: "A2 Fresh Cow Milk",
    adType: "AI Video Advertisement",
    adLanguage: "Telugu",
    videoDuration: "30 seconds",
    deliverablesCount: 2,
    budgetRange: "₹5,000 - ₹10,000",
    agreedPrice: "₹7,500",
    advancePaymentDetails: "50% advance after script approval",
    projectDescription: "Cinematic Telugu AI reel highlighting morning farm freshness.",
    specialInstructions: "Include traditional brass pot visuals.",
    referenceMediaSummary: "2 reference photos attached locally",
    expectedDeliveryDate: "2026-11-30",
    productionAgreementAccepted: true,
    aiContentDisclosureAccepted: true,
    realPersonVoiceConsentAccepted: true,
    clientApprovalChecked: true,
    clientFinalApproved: false,
    clientApprovalAccuracy: true,
    publishClientSocial: false,
    publishAgencySocial: false,
    publishAgencyPortfolio: false,
    publishDoNotPublish: true,
    clientSignature: "Lakshmi Narayana",
    agreementDate: "2026-11-21",
    representativeName: "ALPHA MEDIA Studio Desk",
    projectStatus: "AGREEMENT_ACCEPTED",
    publishingStatus: "NOT_AUTHORIZED",
    paymentStatus: "UNPAID",
    revisionNotes: "",
    deliveryCompleted: false,
    agreementVersion: 1,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated guest: denied private reads/writes, allowed public catalog reads & guest booking/enquiry create", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();

  // Public catalog reads allowed (no client PII)
  await assertSucceeds(unauthDb.collection("portfolio_items").get());
  await assertSucceeds(unauthDb.collection("services").get());

  // Private client collections strictly denied for unauthenticated reads/queries
  await assertFails(unauthDb.collection("bookings").get());
  await assertFails(unauthDb.collection("enquiries").get());
  await assertFails(unauthDb.collection("admins").get());

  // Cannot impersonate an authenticated UID on create
  await assertFails(
    unauthDb.collection("bookings").doc("bk_spoof").set(validBookingPayload(ALICE_UID))
  );

  // Guest can submit a valid booking & initial history event in a batch (`userId == 'guest'`)
  const batch = unauthDb.batch();
  const bookingRef = unauthDb.collection("bookings").doc("bk_guest_1");
  const historyRef = bookingRef.collection("history").doc("evt_guest_1");
  batch.set(bookingRef, validBookingPayload("guest"));
  batch.set(historyRef, {
    bookingId: "bk_guest_1",
    userId: "guest",
    actorEmail: "+919876543210",
    actorRole: "CLIENT",
    actionTitle: "Guest Booking Submitted",
    actionDetail: "Submitted without login",
    agreementVersion: 1,
    createdAt: new Date(Date.now() - 1000),
  });
  await assertSucceeds(batch.commit());

  // Guest cannot read back, update, or delete any booking (even their guest booking)
  await assertFails(bookingRef.get());
  await assertFails(bookingRef.update({ revisionNotes: "Edit", updatedAt: new Date(Date.now() - 500) }));
  await assertFails(bookingRef.delete());

  // Guest can submit a valid business enquiry (`userId == 'guest'`)
  await assertSucceeds(
    unauthDb.collection("enquiries").doc("enq_guest_1").set({
      userId: "guest",
      name: "Guest Client",
      businessName: "Sri Balaji Dairy",
      whatsappNumber: "+919876543210",
      email: "",
      message: "Need a 30s Telugu dairy video advertisement.",
      status: "OPEN",
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(unauthDb.collection("enquiries").doc("enq_guest_1").get());
});

test("Client owner: valid booking create, filtered list, history log, and publishing withdrawal succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  // Create booking without @DocumentId `id` field
  await assertSucceeds(
    aliceDb.collection("bookings").doc("bk_alice_1").set(
      validBookingPayload(ALICE_UID, {
        publishClientSocial: true,
        publishDoNotPublish: false,
        publishingStatus: "AUTHORIZED",
      })
    )
  );

  // Append history event
  await assertSucceeds(
    aliceDb
      .collection("bookings")
      .doc("bk_alice_1")
      .collection("history")
      .doc("evt_1")
      .set({
        bookingId: "bk_alice_1",
        userId: ALICE_UID,
        actorEmail: "alice@example.com",
        actorRole: "CLIENT",
        actionTitle: "Agreement Accepted",
        actionDetail: "Client submitted booking and accepted production agreement.",
        agreementVersion: 1,
        createdAt: new Date(Date.now() - 1000),
      })
  );

  // Filtered query matching Kotlin repository
  await assertSucceeds(
    aliceDb.collection("bookings").where("userId", "==", ALICE_UID).get()
  );

  // Unfiltered query for non-admin must fail
  await assertFails(aliceDb.collection("bookings").get());

  // Withdraw publishing rights anytime
  await assertSucceeds(
    aliceDb.collection("bookings").doc("bk_alice_1").update({
      publishClientSocial: false,
      publishAgencySocial: false,
      publishAgencyPortfolio: false,
      publishDoNotPublish: true,
      publishingStatus: "REVOKED",
      agreementVersion: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Security bounds: reject cross-user access, ghost fields, unverified admin email spoof, and client status spoofing", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("bookings")
      .doc("bk_bob_1")
      .set(validBookingPayload(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  // Cross-user read & update denied
  await assertFails(aliceDb.collection("bookings").doc("bk_bob_1").get());
  await assertFails(
    aliceDb.collection("bookings").doc("bk_bob_1").update({
      revisionNotes: "Hijack",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject @DocumentId `id` or ghost field on create
  await assertFails(
    aliceDb.collection("bookings").doc("bk_ghost").set({
      ...validBookingPayload(ALICE_UID),
      id: "bk_ghost",
    })
  );

  // Reject client marking payment as PAID_IN_FULL on create
  await assertFails(
    aliceDb.collection("bookings").doc("bk_cheat").set(
      validBookingPayload(ALICE_UID, { paymentStatus: "PAID_IN_FULL" })
    )
  );

  // Reject future timestamp (> request.time)
  await assertFails(
    aliceDb.collection("bookings").doc("bk_future").set(
      validBookingPayload(ALICE_UID, {
        createdAt: new Date(Date.now() + 60000),
      })
    )
  );

  // Email spoofing test: matching admin email with email_verified: false MUST fail
  const spoofAdminDb = testEnv.authenticatedContext("spoof_uid", {
    email: ADMIN_EMAIL,
    email_verified: false,
  }).firestore();
  await assertFails(spoofAdminDb.collection("bookings").get());
  await assertFails(
    spoofAdminDb.collection("bookings").doc("bk_bob_1").update({
      paymentStatus: "PAID_IN_FULL",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Verified Admin: can list all bookings, update workflow/payment status, and manage portfolio & services", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("bookings")
      .doc("bk_bob_1")
      .set(validBookingPayload(BOB_UID));
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true,
  }).firestore();

  // Admin can list all bookings
  await assertSucceeds(adminDb.collection("bookings").get());

  // Admin CANNOT set publishingStatus=AUTHORIZED when client set publishDoNotPublish=true
  await assertFails(
    adminDb.collection("bookings").doc("bk_bob_1").update({
      publishingStatus: "AUTHORIZED",
      agreementVersion: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin can advance projectStatus, paymentStatus, and mark deliveryCompleted
  await assertSucceeds(
    adminDb.collection("bookings").doc("bk_bob_1").update({
      projectStatus: "DELIVERED",
      paymentStatus: "PAID_IN_FULL",
      deliveryCompleted: true,
      revisionNotes: "Final 1080p MP4 delivered.",
      agreementVersion: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin can revoke publishingStatus even if client had checked publishClientSocial
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("bookings")
      .doc("bk_bob_pub")
      .set(
        validBookingPayload(BOB_UID, {
          publishClientSocial: true,
          publishDoNotPublish: false,
          publishingStatus: "AUTHORIZED",
        })
      );
  });
  await assertSucceeds(
    adminDb.collection("bookings").doc("bk_bob_pub").update({
      publishingStatus: "REVOKED",
      agreementVersion: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin can manage portfolio_items & services
  const now = new Date(Date.now() - 1000);
  await assertSucceeds(
    adminDb.collection("portfolio_items").doc("port_1").set({
      titleEn: "Godavari Dairy Launch",
      titleTe: "గోదావరి డైరీ ప్రకటన",
      category: "Dairy Advertisements",
      descriptionEn: "AI concept advertisement showcasing pure farm milk.",
      descriptionTe: "స్వచ్ఛమైన పాల నాణ్యతను చూపే AI కాన్సెప్ట్ ప్రకటన.",
      durationLabel: "30s Reel",
      visualKey: "dairy",
      storyboardSteps: ["Sunrise farm", "Pouring milk", "Brand lockup"],
      conceptNoticeEn: "AI Concept Advertisement — Demonstration showcase only.",
      conceptNoticeTe: "AI కాన్సెప్ట్ ప్రకటన — ప్రదర్శన కోసం మాత్రమే.",
      createdBy: ADMIN_UID,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    adminDb.collection("services").doc("srv_1").set({
      orderIndex: 1,
      titleEn: "AI Video Advertisement Production",
      titleTe: "AI వీడియో ప్రకటనల నిర్మాణం",
      descriptionEn: "Studio-grade AI commercial production.",
      descriptionTe: "స్టూడియో స్థాయి AI వాణిజ్య ప్రకటనలు.",
      deliverablesEn: "1080p MP4 + Storyboard",
      deliverablesTe: "1080p వీడియో + స్టోరీబోర్డ్",
      adTypeKey: "AI Video Advertisement",
      createdBy: ADMIN_UID,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Non-admin cannot create portfolio_items
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();
  await assertFails(
    aliceDb.collection("portfolio_items").doc("port_2").set({
      titleEn: "Unauthorized",
      titleTe: "అనధికారం",
      category: "Product Advertisements",
      descriptionEn: "Should fail",
      descriptionTe: "విఫలం కావాలి",
      durationLabel: "15s",
      visualKey: "product",
      storyboardSteps: ["Step 1"],
      conceptNoticeEn: "AI Concept Advertisement",
      conceptNoticeTe: "AI కాన్సెప్ట్ ప్రకటన",
      createdBy: ALICE_UID,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Business Enquiries: owner creates and reads own; admin manages all", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true,
  }).firestore();

  const now = new Date(Date.now() - 1000);
  await assertSucceeds(
    aliceDb.collection("enquiries").doc("enq_1").set({
      userId: ALICE_UID,
      name: "Ravi Kumar",
      businessName: "Pithapuram Silks",
      whatsappNumber: "+919848012345",
      email: "ravi@example.com",
      message: "Need festive Telugu AI reel for Sankranti sale.",
      status: "OPEN",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("enquiries").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("enquiries").get());
  await assertSucceeds(adminDb.collection("enquiries").get());
  await assertSucceeds(
    adminDb.collection("enquiries").doc("enq_1").update({
      status: "RESPONDED",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});
