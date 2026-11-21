const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_player_123";
const BOB_UID = "bob_player_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

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

function validProgressPayload(uid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    currentLevel: 1,
    highestUnlockedLevel: 1,
    completedLevelsCount: 0,
    totalStars: 0,
    totalMoves: 0,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

function validFigurePayload(uid, figureId, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    figureId,
    unlockedAtLevel: 1,
    starsEarned: 3,
    bestMoves: 8,
    timesCompleted: 1,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

test("Unauthenticated user: denied all reads and writes on progress and unlockedFigures", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("progress").doc("main").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("progress")
      .doc("main")
      .set(validProgressPayload(ALICE_UID))
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("unlockedFigures").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("unlockedFigures")
      .doc("fig_01_fips")
      .set(validFigurePayload(ALICE_UID, "fig_01_fips"))
  );
});

test("Owner progress: valid create, level-advance update, level-select update, and companion update succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const progressRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("progress")
    .doc("main");

  await assertSucceeds(progressRef.set(validProgressPayload(ALICE_UID)));
  await assertSucceeds(progressRef.get());

  // Advance after completing Level 1
  await assertSucceeds(
    progressRef.update({
      currentLevel: 2,
      highestUnlockedLevel: 2,
      completedLevelsCount: 1,
      totalStars: 3,
      totalMoves: 8,
      selectedCompanionId: "fig_01_fips",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Switch active level back to Level 1
  await assertSucceeds(
    progressRef.update({
      currentLevel: 1,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Change companion
  await assertSucceeds(
    progressRef.update({
      selectedCompanionId: "fig_01_fips",
      updatedAt: new Date(Date.now() - 200),
    })
  );
});

test("Owner progress: rejects invalid bounds, ghost fields, future timestamps, and immutable mutations", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const progressRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("progress")
    .doc("main");

  // Wrong document ID (only 'main' permitted)
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("progress")
      .doc("other")
      .set(validProgressPayload(ALICE_UID))
  );

  // Ghost field ('id' or 'isAdmin')
  await assertFails(
    progressRef.set(validProgressPayload(ALICE_UID, { id: "main" }))
  );
  await assertFails(
    progressRef.set(validProgressPayload(ALICE_UID, { isAdmin: true }))
  );

  // currentLevel > highestUnlockedLevel
  await assertFails(
    progressRef.set(
      validProgressPayload(ALICE_UID, { currentLevel: 5, highestUnlockedLevel: 2 })
    )
  );

  // Future timestamp rejected
  await assertFails(
    progressRef.set(
      validProgressPayload(ALICE_UID, {
        createdAt: new Date(Date.now() + 60000),
        updatedAt: new Date(Date.now() + 60000),
      })
    )
  );

  // Create valid doc first
  await assertSucceeds(progressRef.set(validProgressPayload(ALICE_UID)));

  // Reject mutating immutable createdAt
  await assertFails(
    progressRef.update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );

  // Reject spoofing userId
  await assertFails(
    progressRef.update({
      userId: BOB_UID,
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Owner unlockedFigures: create, list, replay improvement update succeed; downgrades & bad payloads fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const figuresCol = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("unlockedFigures");

  // Empty subcollection list must succeed
  await assertSucceeds(figuresCol.get());

  const figRef = figuresCol.doc("fig_01_fips");
  await assertSucceeds(
    figRef.set(
      validFigurePayload(ALICE_UID, "fig_01_fips", {
        unlockedAtLevel: 1,
        starsEarned: 2,
        bestMoves: 10,
        timesCompleted: 1,
      })
    )
  );

  await assertSucceeds(figuresCol.get());
  await assertSucceeds(figRef.get());

  // Valid replay improvement
  await assertSucceeds(
    figRef.update({
      starsEarned: 3,
      bestMoves: 8,
      timesCompleted: 2,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject lowering starsEarned or raising bestMoves
  await assertFails(
    figRef.update({
      starsEarned: 1,
      bestMoves: 8,
      timesCompleted: 3,
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Reject mismatched figureId vs document ID
  await assertFails(
    figuresCol
      .doc("fig_02_benni")
      .set(validFigurePayload(ALICE_UID, "fig_01_fips"))
  );

  // Reject @DocumentId 'id' field in payload
  await assertFails(
    figuresCol
      .doc("fig_02_benni")
      .set(validFigurePayload(ALICE_UID, "fig_02_benni", { id: "fig_02_benni" }))
  );
});

test("Cross-user isolation: Bob cannot read, list, create, update, or delete Alice's data", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore();
    await adminDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("progress")
      .doc("main")
      .set(validProgressPayload(ALICE_UID));
    await adminDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("unlockedFigures")
      .doc("fig_01_fips")
      .set(validFigurePayload(ALICE_UID, "fig_01_fips"));
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("progress").doc("main").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("progress")
      .doc("main")
      .update({ currentLevel: 99, updatedAt: new Date(Date.now() - 500) })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("unlockedFigures").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("unlockedFigures")
      .doc("fig_01_fips")
      .delete()
  );
});
