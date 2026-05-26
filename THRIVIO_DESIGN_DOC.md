# THRIVIO — Complete Design & Architecture Document

## Version 1.0 | May 2026

---

## 1. PRODUCT VISION

**Thrivio** is a unified health super-app combining **Fitness**, **Nutrition**, and **Mental Health** into a single gamified experience. The design language fuses **Duolingo's playful reward system** (streaks, XP, leagues, 3D buttons, mascot) with **iOS glassmorphism** (frosted glass cards, vibrancy, rounded corners, SF-symbol-like icons).

**Core Loop:**  
`Track → Earn XP → Level Up → Unlock Rewards → Stay Motivated → Track More`

**Mascot:** *Aero* — a friendly animated astronaut/panda guide who celebrates wins, gives nudges, and evolves with the user.

**Revenue Model:** Single lifetime purchase (₹499) via **Razorpay/PhonePe** unlocking premium features (unlimited streak freezes, exclusive avatars, diamond leagues, advanced analytics).

---

## 2. UI/UX DESIGN LANGUAGE

### 2.1 Color System (Already Defined)

| Token | Hex | Usage |
|---|---|---|
| `MascotGreen` | `#58CC02` | Primary actions, success, progress |
| `XpOrange` | `#FF9600` | XP, streaks, premium highlights |
| `FitnessBlue` | `#1899D6` | Steps, workouts, activity rings |
| `MindPurple` | `#8E44AD` | Meditation, mental health, sleep |
| `WaterBlue` | `#3498DB` | Hydration tracking |
| `LightBackground` | `#F7F9FA` | iOS light mode background |
| `DarkCharcoal` | `#15191C` | Dark mode background |
| `SurfaceCardLight` | `#FFFFFF` | Glassmorphic card surface (light) |
| `SurfaceCardDark` | `#242B30` | Glassmorphic card surface (dark) |

### 2.2 Typography

- **Family:** System Sans-Serif (San Francisco on iOS, Roboto on Android)
- **Weights:** Black (900) for headings, ExtraBold (800) for titles, Bold (700) for buttons, Medium (500) for body
- **Sizes:** Display 32sp, Title 22sp, Body 16sp, Caption 12sp

### 2.3 iOS + Duolingo Fusion Principles

| Principle | Implementation |
|---|---|
| **Glassmorphism** | Cards with `alpha=0.95`, backdrop blur, 1dp white border, 16dp rounded corners, soft shadow |
| **3D Tactile Buttons** | Two-layer compose: shadow base + pressable top that animates 4dp down on press + haptic feedback |
| **Bouncy Animations** | Spring-based scale/stagger on XP pop-ups, streak fire, level-ups |
| **Circular Progress** | Concentric rings for steps/calories/sleep (already built in Canvas) |
| **Bottom Nav Bar** | Glassmorphic floating pill — 5 tabs: Home, Workout, Nutrition, Mind, Profile |
| **Mascot Integration** | Aero appears in header, achievement celebrations, empty states, nudges |
| **Haptic Feedback** | On every reward, button press, streak milestone, water log |

### 2.4 Screen Map

```
┌─────────────────────────────────────────────────┐
│  SPLASH → ONBOARDING (3 slides) → AUTH          │
├─────────────────────────────────────────────────┤
│  AUTH SCREEN                                     │
│  ├─ "Continue with Google"                       │
│  ├─ "Continue with Email"                        │
│  └─ Or sign up → Health Connect permission req   │
├─────────────────────────────────────────────────┤
│  HOME DASHBOARD (Main Screen)                    │
│  ├─ Header: Aero mascot + greeting + XP + streak │
│  ├─ Streak Hero Card (gradient, tappable)        │
│  ├─ Daily Goals Rings (steps/kcal/sleep/water)   │
│  ├─ Today at a Glance (workout/meal summary)     │
│  ├─ Hydration Log (interactive +/-)              │
│  ├─ Quick Start Workout / Meditate buttons       │
│  └─ Active Challenges carousel                   │
├─────────────────────────────────────────────────┤
│  WORKOUT TAB                                     │
│  ├─ Exercise library (search by category/muscle) │
│  ├─ Workout builder (custom routines)            │
│  ├─ Quick Start: 7-min, Push Pull, Full Body     │
│  ├─ Active workout screen (timer, reps, sets)    │
│  └─ Workout history & progress charts            │
├─────────────────────────────────────────────────┤
│  NUTRITION TAB                                   │
│  ├─ Daily meal log (breakfast/lunch/dinner/snack)│
│  ├─ Barcode scanner (calorie lookup)             │
│  ├─ Macro rings (protein/carbs/fats)             │
│  ├─ Water tracking                               │
│  └─ Meal suggestions / recipes                   │
├─────────────────────────────────────────────────┤
│  MIND TAB                                        │
│  ├─ Guided breathing (4-4-4 box breathing)       │
│  ├─ Mood log (emoji picker daily)                │
│  ├─ Meditation timer (with ambient sounds)       │
│  ├─ Sleep tracking summary                        │
│  └─ Daily affirmation / gratitude journal         │
├─────────────────────────────────────────────────┤
│  PROFILE / SOCIAL TAB                            │
│  ├─ User profile (avatar, stats, level)          │
│  ├─ Friends list + add friend (by username)      │
│  ├─ Leaderboard (weekly/monthly)                 │
│  ├─ Challenges (join/compete)                    │
│  ├─ Achievements / Badges wall                   │
│  ├─ Premium → Lifetime purchase paywall          │
│  └─ Settings (goals, notifications, privacy)     │
└─────────────────────────────────────────────────┘
```

### 2.5 Key UI Components to Build

| Component | Description |
|---|---|
| `MascotAvatar` | Animated Aero (PNG sequence or Lottie) |
| `XpPopup` | "+25 XP" fly-away text with spring animation |
| `StreakFlame` | Animated fire icon + day count |
| `GoalRing` | Reusable Canvas ring with gradient stroke |
| `GlassyCard` | Frosted glass container (SurfaceCard + border + shadow) |
| `TactileButton` | Already built — 3D press + haptic |
| `BouncyCheckmark` | Spring-animated check circle |
| `ChallengeCard` | Progress bar + timer + participants count |
| `PremiumBadge` | Crown icon for premium users |
| `BarcodeScanner` | Camera-based food lookup |
| `MeditationPlayer` | Timer + ambient audio controls |
| `MacroPieChart` | Protein/Carbs/Fat distribution |

---

## 3. SYSTEM ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────────┐
│                        THRIVIO ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────┐                       │
│  │         ANDROID CLIENT (Kotlin/Compose)│                     │
│  │  ┌─────┐ ┌──────┐ ┌──────┐ ┌──────┐  │                     │
│  │  │ UI  │ │SERVICES│ │SYNC  │ │CACHE │  │                     │
│  │  │Layer│ │(Step, │ │Engine│ │(Room)│  │                     │
│  │  │     │ │FCM)   │ │      │ │DB    │  │                     │
│  │  └─────┘ └──────┘ └──────┘ └──────┘  │                     │
│  └──────────────┬───────────────────────┘                       │
│                 │ Supabase SDK + Ktor HTTP                      │
├─────────────────┼───────────────────────────────────────────────┤
│                 ▼                                               │
│  ┌──────────────────────────────────────────────────────┐       │
│  │               SUPABASE BACKEND (BaaS)                 │       │
│  │  ┌────────────┐ ┌──────────┐ ┌──────────────────┐   │       │
│  │  │ PostgreSQL │ │ GoTrue   │ │ Realtime (WS)    │   │       │
│  │  │ (Data)     │ │ (Auth)   │ │ (Live sync)      │   │       │
│  │  │ 20+ tables │ │Google/   │ │ Challenges,      │   │       │
│  │  │ RLS + Trig │ │Email     │ │ Messages, Notifs  │   │       │
│  │  └────────────┘ └──────────┘ └──────────────────┘   │       │
│  │  ┌──────────────────────────────────────────────┐   │       │
│  │  │ Supabase Edge Functions (Deno/TypeScript)     │   │       │
│  │  │ ├─ payment-webhook (verify Razorpay payment) │   │       │
│  │  │ ├─ daily-reset (midnight step/food rollover) │   │       │
│  │  │ ├─ challenge-eval (end-of-challenge rewards)  │   │       │
│  │  │ └─ push-notification (scheduled reminders)    │   │       │
│  │  └──────────────────────────────────────────────┘   │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  ┌────────────────────┐   ┌──────────────────┐                  │
│  │   FIREBASE          │   │   IMAGEKIT.IO    │                  │
│  │   ├─ FCM (Push)     │   │   ├─ Avatars     │                  │
│  │   └─ Analytics      │   │   ├─ Food Photos │                  │
│  │                     │   │   └─ Progress Pix │                  │
│  └────────────────────┘   └──────────────────┘                  │
│                                                                  │
│  ┌──────────────────────────────────────────────┐               │
│  │   HEALTH CONNECT / GOOGLE FIT (Android)       │               │
│  │   ├─ Read steps, calories, distance            │               │
│  │   ├─ Read sleep sessions                       │               │
│  │   └─ Read heart rate (if available)            │               │
│  └──────────────────────────────────────────────┘               │
│                                                                  │
│  ┌────────────────────┐                                         │
│  │   RAZORPAY/PHONEPE  │                                         │
│  │   ├─ One-time       │                                         │
│  │   │  lifetime ₹499  │                                         │
│  │   └─ Webhook →      │                                         │
│  │      Supabase EF     │                                         │
│  └────────────────────┘                                         │
└─────────────────────────────────────────────────────────────────┘
```

### 3.1 Technology Stack Justification

| Service | Why | Cost |
|---|---|---|
| **Supabase** | Free tier: 500MB DB, 50k users, 2GB bandwidth, GoTrue auth, Realtime, Edge Functions. Single vendor for auth+DB+API+serverless. | **Free** |
| **Firebase FCM** | Free push notifications. Already integrated. | **Free** |
| **ImageKit.io** | Free tier: 20GB storage, 20GB bandwidth/month. Optimized image transformations for avatars/food photos. | **Free** |
| **Razorpay** | Zero setup fee, 2% per transaction. Only used for lifetime purchase. | ~₹10/transaction |
| **GitHub** | Free private repos, Actions CI/CD (2000 min/month free). | **Free** |
| **Health Connect** | Android native API, no server cost. | **Free** |

**Total monthly infra cost at launch: ₹0** (well within all free tiers)
**Estimated annual cost at 10k users:** ₹0–500 (if exceeding Supabase free bandwidth)

---

## 4. DATA MODEL (Enhanced)

All tables already defined in `supabase/schema.sql`. Below are the additions/enhancements needed:

### 4.1 New / Modified Tables

```sql
-- ==========================================
-- ADD TO SCHEMA: Enhanced Gamification
-- ==========================================

-- User Levels (computed from XP)
CREATE TABLE IF NOT EXISTS public.user_levels (
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE PRIMARY KEY,
    level INTEGER DEFAULT 1 NOT NULL,
    current_xp INTEGER DEFAULT 0 NOT NULL,
    xp_to_next_level INTEGER DEFAULT 100 NOT NULL,
    total_xp_earned INTEGER DEFAULT 0 NOT NULL
);

-- Badges / Achievements
CREATE TABLE IF NOT EXISTS public.achievements (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    badge_id TEXT NOT NULL, -- 'first_workout', '7_day_streak', '100k_steps', etc
    badge_name TEXT NOT NULL,
    badge_icon TEXT NOT NULL,
    unlocked_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    CONSTRAINT unique_user_badge UNIQUE(user_id, badge_id)
);

-- Daily aggregated stats (for performance)
CREATE TABLE IF NOT EXISTS public.daily_stats (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    date DATE DEFAULT CURRENT_DATE NOT NULL,
    steps INTEGER DEFAULT 0,
    calories_burned INTEGER DEFAULT 0,
    calories_consumed INTEGER DEFAULT 0,
    protein_grams REAL DEFAULT 0,
    water_cups INTEGER DEFAULT 0,
    sleep_hours REAL DEFAULT 0,
    meditation_minutes INTEGER DEFAULT 0,
    mood_score INTEGER, -- 1-5
    xp_earned INTEGER DEFAULT 0,
    CONSTRAINT unique_user_date_stat UNIQUE(user_id, date)
);

-- Mood Logs
CREATE TABLE IF NOT EXISTS public.mood_logs (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    date DATE DEFAULT CURRENT_DATE NOT NULL,
    mood INTEGER NOT NULL, -- 1=terrible..5=amazing
    note TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Meditation Sessions
CREATE TABLE IF NOT EXISTS public.meditation_sessions (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    duration_seconds INTEGER NOT NULL,
    type TEXT DEFAULT 'breathing', -- 'breathing', 'guided', 'free'
    completed_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Premium Purchases
CREATE TABLE IF NOT EXISTS public.premium_purchases (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    order_id TEXT UNIQUE NOT NULL,
    payment_provider TEXT NOT NULL, -- 'razorpay' or 'phonepay'
    amount INTEGER NOT NULL, -- in paise (49900 = ₹499)
    status TEXT DEFAULT 'pending' NOT NULL, -- 'pending', 'completed', 'refunded'
    purchased_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);
```

### 4.2 Row Level Security (RLS) for New Tables

All new tables follow the same pattern: `auth.uid() = user_id` for ownership.

### 4.3 Database Indexes for Performance

```sql
CREATE INDEX IF NOT EXISTS idx_daily_stats_user_date ON public.daily_stats(user_id, date);
CREATE INDEX IF NOT EXISTS idx_user_xp_user_date ON public.user_xp(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_step_counts_user_date ON public.step_counts(user_id, date);
CREATE INDEX IF NOT EXISTS idx_nutrition_user_date ON public.nutrition(user_id, date);
CREATE INDEX IF NOT EXISTS idx_meals_user_date ON public.meals(user_id, date);
CREATE INDEX IF NOT EXISTS idx_messages_pair ON public.messages(sender_id, receiver_id, created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON public.notifications(user_id, is_read);
```

---

## 5. AUTHENTICATION SYSTEM

### 5.1 Supported Methods

| Method | Implementation | Status |
|---|---|---|
| **Google Sign-In** | Supabase GoTrue + Google OAuth + Android Credential Manager | In-progress |
| **Email/Password** | Supabase GoTrue built-in | In-progress |
| **Magic Link** | Future enhancement | Planned |

### 5.2 Auth Flow

```
User opens app
  ↓
[ONBOARDING] → 3 screens (Value prop, Health Connect request, Goals setup)
  ↓
[AUTH SCREEN]
  ├─ Big green "Continue with Google" button (TactileButton)
  ├─ Divider "or"
  └─ Email input → Password input → "Sign Up" / "Log In"
  ↓
[ONBOARDING COMPLETE] → Health Connect permission request (if not granted)
  ↓
[HOME DASHBOARD]
```

### 5.3 Auth Implementation Steps

1. Integrate Supabase GoTrue with Google OAuth in Android
2. Use Android Credential Manager for Google One Tap
3. On sign-up, trigger `handle_new_user()` (already defined in schema)
4. Store FCM token in `device_tokens` table after auth
5. Implement session persistence (Supabase client handles JWT refresh)

---

## 6. AUTOMATIC TRACKING SYSTEM

### 6.1 Sensors & Data Sources

| Data | Source | Logic |
|---|---|---|
| **Steps** | Hardware `TYPE_STEP_COUNTER` | Already built in `StepCounterService.kt` — records steps since device boot, computes offset |
| **Calories Burned** | Health Connect API | Read `TotalCaloriesBurned` from Health Connect (more accurate than estimation) |
| **Distance** | Health Connect / GPS | Read distance from Health Connect; fallback = steps × stride length |
| **Sleep** | Health Connect API | Read `SleepSession` data |
| **Heart Rate** | Health Connect API | Optional — for premium analytics |
| **Food** | Barcode scanner + user input | CameraX barcode scanning → lookup in `food_items` DB |
| **Workouts** | User input + Health Connect | Manual log or import from Health Connect workout sessions |
| **Mood** | User input (emoji picker) | Daily mood-log prompt at end of day |
| **Water** | User input (+/- buttons) | Already built in Dashboard |

### 6.2 Sync Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌────────────────┐
│  Hardware Sensor│────>│ StepCounterSvc  │────>│ Supabase DB    │
│  (TYPE_STEP_    │     │ (Background)    │     │ step_counts    │
│   COUNTER)      │     │ Debounce 50 step│     │ (upsert daily) │
└─────────────────┘     └─────────────────┘     └────────────────┘
                               │
┌─────────────────┐            │
│  Health Connect │────────────┤
│  (Read APIs)    │            │
│  Steps/Kcal/    │            ▼
│  Sleep/Workout  │     ┌────────────────┐
└─────────────────┘     │ SyncManager    │
                        │ (Coroutine)    │
┌─────────────────┐     │ - 15-min ticks │
│  Room DB (Local)│<────│ - Debounce     │
│  (Offline cache)│     │ - Conflict res │
└─────────────────┘     └────────────────┘
```

### 6.3 Offline-First Strategy

- **Room database** mirrors Supabase tables locally
- All sensor data writes to Room first, then syncs to Supabase
- On reconnect, push local changes, pull remote changes
- Conflict resolution: latest `updated_at` wins

### 6.4 Background Constraints (Android 14+)

- `FOREGROUND_SERVICE_HEALTH` permission declared
- `StepCounterService` runs as foreground with persistent notification
- Minimum 15-minute worker for Health Connect data sync
- Battery optimization exemption requested on first launch

---

## 7. GAMIFICATION ENGINE

### 7.1 Core Mechanics

| Mechanic | Detail |
|---|---|
| **XP** | Earned for every tracked activity: +10 per 1000 steps, +50 per workout, +25 per meal logged, +15 per meditation session, +5 per water cup |
| **Streaks** | Consecutive days meeting step goal (8000). Frozen with premium. |
| **Levels** | 1–100+. XP needed = `100 × level × 1.5`. Each level unlocks new features/avatars. |
| **Badges** | 50+ achievements: "First Steps", "Centurion (100 days)", "Marathoner", "Calorie Counter", etc. |
| **Leagues** | Bronze → Silver → Gold → Sapphire → Ruby → Diamond. Weekly reset based on XP earned. Top 10 advance. |
| **Leaderboards** | Friends-only weekly ranking by steps, XP, workouts |
| **Challenges** | Community-wide: "10k steps for 7 days", "Log 20 meals this week". Rewards XP + exclusive badges. |

### 7.2 XP Reward Table

| Action | XP | Daily Max |
|---|---|---|
| Complete step goal (8k) | 50 | 50 |
| Every 1k steps after goal | 5 | 25 |
| Log a meal | 25 | 75 |
| Complete a workout | 50 | 150 |
| Meditate (5+ min) | 30 | 60 |
| Log water (per cup) | 5 | 40 |
| Log mood | 10 | 10 |
| Maintain streak (per day) | 20 × streak_day | No cap |
| Complete a challenge | 200–1000 | Per challenge |
| Win league promotion | 500 | Weekly |

### 7.3 Premium vs Free

| Feature | Free | Premium (Lifetime ₹499) |
|---|---|---|
| Step/calorie tracking | ✅ | ✅ |
| Basic workouts | ✅ | ✅ |
| Nutrition logging | ✅ | ✅ |
| Breathing exercise | ✅ | ✅ |
| Streak freeze | 1 day/month | Unlimited |
| Avatars | 3 basic | 20+ exclusive |
| Leagues | Bronze–Gold | All leagues (Sapphire–Diamond) |
| Advanced analytics | — | Weekly trends, body composition charts |
| No ads | — | ✅ |
| Backup/export | — | ✅ |

---

## 8. PAYMENT SYSTEM

### 8.1 Flow

```
User taps "Unlock Lifetime" in Profile or Paywall dialog
  ↓
App calls Razorpay SDK with order params:
  - amount: 49900 (₹499 in paise)
  - currency: INR
  - receipt: user_id + timestamp
  ↓
User completes UPI/Card/Netbanking via Razorpay Checkout
  ↓
Razorpay returns payment_id + order_id + signature
  ↓
App sends to Supabase Edge Function: /payment/verify
  ↓
Edge Function verifies signature with Razorpay secret
  ↓
On success:
  - Insert into premium_purchases
  - Update profiles.is_premium = true
  - Send push notification "🏆 Premium unlocked!"
  ↓
App refreshes → Premium features activated
```

### 8.2 Supabase Edge Function: payment-verify

```typescript
// supabase/functions/payment-verify/index.ts
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { validatePaymentVerification } from "https://esm.sh/razorpay@2.9.2/dist/razorpay"

serve(async (req) => {
  const { payment_id, order_id, signature, user_id } = await req.json()
  
  const isValid = validatePaymentVerification(
    { order_id, payment_id },
    signature,
    Deno.env.get("RAZORPAY_KEY_SECRET")!
  )
  
  if (!isValid) return new Response("Invalid signature", { status: 400 })
  
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  )
  
  await supabase.from("premium_purchases").insert({
    user_id, order_id, payment_provider: "razorpay",
    amount: 49900, status: "completed"
  })
  
  await supabase.from("profiles").update({ is_premium: true }).eq("id", user_id)
  
  return new Response(JSON.stringify({ success: true }))
})
```

### 8.3 Razorpay Setup (No Backend Needed for SDK)

- Android SDK: `implementation("com.razorpay:checkout:1.6.38")`
- API Key: Set in Android code (public key only, safe for client)
- Webhook Secret: Stored as Edge Function environment variable (never in app)

---

## 9. HEALTH CONNECT / GOOGLE FIT INTEGRATION

### 9.1 Setup Completed

- `AndroidManifest.xml` declares `HealthConnectPermissionActivity`
- `arrays.xml` defines requested permissions: Steps (R/W), TotalCaloriesBurned (R/W), SleepSession (R/W)
- Future: HeartRate, Distance, WorkoutSession

### 9.2 Sync Implementation (To Build)

```kotlin
// New file: HealthConnectSyncManager.kt
class HealthConnectSyncManager(private val context: Context) {
    
    suspend fun syncSteps(): Int {
        val healthClient = HealthConnectClient.getOrCreate(context)
        val response = healthClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRange = todayRange()
            )
        )
        val totalSteps = response.records.sumOf { it.count }
        // Upsert to local Room + Supabase
        return totalSteps
    }
    
    suspend fun syncSleep(): Float { /* similar for SleepSessionRecord */ }
    suspend fun syncCalories(): Int { /* similar for TotalCaloriesBurnedRecord */ }
    
    // Called every 15 min via WorkManager
    suspend fun syncAll() {
        val steps = syncSteps()
        val calories = syncCalories()
        val sleep = syncSleep()
        // Batch upsert to Supabase
    }
}
```

### 9.3 Schedule

- **WorkManager PeriodicWorkRequest**: 15-minute interval
- **Constraints**: `NetworkType.CONNECTED` (only sync when online)
- **On first launch**: immediate sync + request permissions

---

## 10. NOTIFICATION ENGINE

### 10.1 Notification Types

| Type | Trigger | Schedule |
|---|---|---|
| **Morning Reminder** | "Aero says good morning! Ready to hit your steps?" | 7 AM daily |
| **Streak At Risk** | "You haven't walked in 4 hours! Streak freeze available ⚡" | 8 PM if steps < 50% |
| **Workout Suggestion** | Based on user history | 5 PM daily |
| **Meal Reminder** | "Did you log your lunch today?" | 2 PM |
| **Challenge Update** | "You're #3 in the 10k challenge!" | Event-based |
| **Achievement Unlocked** | "You earned 'Centurion' badge!" | Event-based |
| **Premium Success** | "Welcome to Diamond League!" | After purchase |

### 10.2 Delivery Architecture

```
Supabase Edge Function (scheduler)
  ↓ HTTP POST
Firebase FCM API
  ↓ Push
Android device → ThrivioFcmService.onMessageReceived()
  ↓
Show notification + Track in notifications table
```

---

## 11. IMAGEKIT.IO STORAGE

### 11.1 Usage

| Content | Folder Path | Transformations |
|---|---|---|
| Avatar photos | `/users/{user_id}/avatar` | `tr:w-200,h-200,fo-face` |
| Meal photos | `/users/{user_id}/meals/{meal_id}` | `tr:w-800,h-800` |
| Progress photos | `/users/{user_id}/progress/{date}` | `tr:w-1200,h-1200` |
| Exercise GIFs | `/exercises/` | None (upload once) |

### 11.2 Integration

- **Android SDK**: Direct upload from app using ImageKit.io Android SDK
- **Signed URLs**: Generate upload signature via Supabase Edge Function
- **Default avatar**: Already referenced in schema trigger as `https://ik.imagekit.io/default_avatar.png`

---

## 12. PERFORMANCE & OFFLINE

### 12.1 Room Database Schema (Android Local)

Mirrors Supabase but optimized for local queries:

```
@Entity(tableName = "local_step_counts")
@Entity(tableName = "local_meals")
@Entity(tableName = "local_workouts")
@Entity(tableName = "local_mood_logs")
@Entity(tableName = "local_nutrition")
```

### 12.2 Data Flow

```
User Action → Room (immediate) → UI updates from Room → Coroutine syncs to Supabase → Server response → Update Room + UI
```

### 12.3 Caching Strategy

| Data | Cache TTL | Staleness Policy |
|---|---|---|
| Step counts | Real-time (sensor) | Always fresh |
| Daily goals | 1 day | Show stale, sync in background |
| Exercise library | 7 days | Refresh on app open |
| Food database | 30 days | Refresh on search |
| Leaderboards | 15 min | Pull-to-refresh |
| Friends list | 5 min | Realtime subscription |

---

## 13. COST OPTIMIZATION PLAN

### 13.1 Supabase Free Tier Budget

| Resource | Free Limit | Expected Usage at 1k Users |
|---|---|---|
| Database | 500MB | ~100MB (text + UUIDs, no images) |
| Auth users | 50,000 | 1,000 ✅ |
| Bandwidth | 2GB/month | ~500MB ✅ |
| Edge Functions | 500k invocations | ~50k/month ✅ |
| Realtime connections | 200 | ~50 concurrent ✅ |

### 13.2 Cost-Saving Decisions

1. **No Node.js server** — Supabase Edge Functions (Deno) replace all backend needs
2. **No paid image hosting** — ImageKit.io free tier is sufficient
3. **No SMS/paid notifications** — Firebase FCM is free
4. **No external analytics** — Firebase Analytics free tier
5. **No CDN needed** — Supabase + ImageKit handle this
6. **Single developer** — Clear architecture reduces onboarding overhead
7. **Jetpack Compose** — Single codebase for Android; iOS = separate project later

### 13.3 When to Scale

| Trigger | Upgrade Path | Estimated Cost |
|---|---|---|
| >50k users | Supabase Pro ($25/mo) | $25/mo |
| >2GB bandwidth | Supabase Pro ($25/mo) | $25/mo |
| >500k Edge Functions | Supabase Pro ($25/mo) | $25/mo |
| Need iOS | React Native or Swift rewrite | Dev cost |
| Need AI coaching | OpenAI API (~$0.01/call) | Variable |

---

## 14. DEVELOPMENT ROADMAP

### Phase 1: Foundation (Week 1–2)
- [x] Project scaffold (Android + Compose)
- [x] Supabase schema deployed
- [x] Step counter service
- [x] Firebase FCM setup
- [ ] Google auth + email auth UI
- [ ] Room database setup
- [ ] Health Connect read + sync

### Phase 2: Core Features (Week 3–4)
- [ ] Full dashboard with live data
- [ ] Nutrition logging (meal add, barcode)
- [ ] Workout library + workout logger
- [ ] Meditation + breathing + mood log
- [ ] XP engine + level system
- [ ] Streak tracking + persistence

### Phase 3: Gamification (Week 5–6)
- [ ] Achievements / badges system
- [ ] Leagues with weekly reset
- [ ] Friends + leaderboard
- [ ] Challenges
- [ ] Aero mascot animations
- [ ] Reward popups + celebrations

### Phase 4: Premium & Polish (Week 7–8)
- [ ] Razorpay/PhonePe integration
- [ ] Premium feature gating
- [ ] ImageKit.io uploads
- [ ] Edge Functions: payment verify, daily reset, push scheduler
- [ ] Dark mode polish
- [ ] UI micro-animations (Lottie/springs)

### Phase 5: Launch (Week 9–10)
- [ ] Play Store listing, screenshots, privacy policy
- [ ] Performance profiling (leak canary, strict mode)
- [ ] Beta testing (Firebase App Distribution)
- [ ] Crash reporting (Firebase Crashlytics)
- [ ] Production release (v1.0.0)

---

## 15. FOLDER STRUCTURE (PROPOSED)

```
app/src/main/java/com/thrivio/
├── MainActivity.kt
├── HealthConnectPermissionActivity.kt
├── ThrivioApp.kt                    # Application class (DI init)
├── auth/
│   ├── AuthScreen.kt
│   ├── AuthViewModel.kt
│   └── GoogleSignInManager.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt           # Room DB
│   │   ├── dao/
│   │   │   ├── StepDao.kt
│   │   │   ├── MealDao.kt
│   │   │   ├── WorkoutDao.kt
│   │   │   └── MoodDao.kt
│   │   └── entity/
│   │       ├── StepEntity.kt
│   │       ├── MealEntity.kt
│   │       ├── WorkoutEntity.kt
│   │       └── MoodEntity.kt
│   ├── remote/
│   │   ├── SupabaseClient.kt        # Existing
│   │   └── dto/                     # API response models
│   └── repository/
│       ├── StepRepository.kt
│       ├── NutritionRepository.kt
│       ├── WorkoutRepository.kt
│       ├── MoodRepository.kt
│       └── GamificationRepository.kt
├── di/                              # Manual DI or Hilt
│   └── AppModule.kt
├── network/
│   └── SupabaseClient.kt            # Existing
├── service/
│   ├── StepCounterService.kt        # Existing
│   ├── ThrivioFcmService.kt         # Existing
│   ├── HealthConnectSyncManager.kt
│   └── NotificationScheduler.kt
├── ui/
│   ├── components/                  # Shared composables
│   │   ├── TactileButton.kt         # Existing
│   │   ├── GlassyCard.kt
│   │   ├── GoalRing.kt
│   │   ├── XpPopup.kt
│   │   ├── MascotAvatar.kt
│   │   └── PremiumBadge.kt
│   ├── navigation/
│   │   └── AppNavigation.kt
│   ├── screens/
│   │   ├── splash/
│   │   ├── onboarding/
│   │   ├── auth/
│   │   ├── dashboard/
│   │   │   ├── DashboardScreen.kt   # Existing
│   │   │   └── DashboardViewModel.kt
│   │   ├── workout/
│   │   ├── nutrition/
│   │   ├── mind/
│   │   └── profile/
│   └── theme/
│       ├── Color.kt                 # Existing
│       ├── Theme.kt                 # Existing
│       └── Type.kt                  # Existing
```

---

## 16. KEY METRICS TO TRACK

| Metric | Tool | Why |
|---|---|---|
| DAU/MAU | Firebase Analytics | Stickiness |
| Step completion rate | Supabase query | Core engagement |
| Meal log frequency | Supabase query | Nutrition engagement |
| Premium conversion | Razorpay + DB | Revenue |
| Streak retention | Supabase query | Gamification effectiveness |
| Crash-free rate | Firebase Crashlytics | Quality |
| App launch time | Firebase Performance | UX |
| Sync success rate | Custom logging | Background reliability |

---

## 17. MAINTENANCE PLAN

### Daily
- Monitor Firebase Crashlytics for new issues
- Check Supabase database size and auth users

### Weekly
- Review Edge Function logs for errors
- Check Razorpay payment webhook success rate
- Verify Health Connect sync success rate

### Monthly
- Review Supabase usage (DB size, bandwidth)
- Update exercise library / food database
- Rotate API keys if needed
- Release Play Store update with bug fixes

### On-Demand
- Add new badges/achievements
- Create new challenges
- Update default goals if user data suggests better targets

---

## 18. RISKS & MITIGATION

| Risk | Mitigation |
|---|---|
| Android 15+ foreground service restrictions | Use `FOREGROUND_SERVICE_HEALTH` type + WorkManager fallback |
| Health Connect API changes | Abstract behind `HeathSyncManager` interface; swap implementation |
| Supabase free tier limits | Add monitoring; budget $25/mo for Pro tier |
| Sensor inaccuracy (step counter) | Cross-validate with Health Connect; show "estimated" label |
| User churn after 7 days | Notification sequence + challenge invites + friend referrals |
| Payment failure | Webhook retry; manual support email for verification |
