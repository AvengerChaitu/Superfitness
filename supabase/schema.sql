-- ====================================================================
-- THRIVIO MASTER DATABASE SCHEMA
-- Execute this script inside your Supabase SQL Editor (https://supabase.com)
-- ====================================================================

-- ==========================================
-- 0. CLEAN EVERYTING (FRESH BUILD DROP ACTIONS)
-- ==========================================
drop trigger if exists on_auth_user_created on auth.users;
drop function if exists public.handle_new_user cascade;

drop table if exists public.meal_foods cascade;
drop table if exists public.meals cascade;
drop table if exists public.food_items cascade;
drop table if exists public.exercise_sets cascade;
drop table if exists public.workout_exercises cascade;
drop table if exists public.workouts cascade;
drop table if exists public.exercises cascade;
drop table if exists public.step_counts cascade;
drop table if exists public.sleep_records cascade;
drop table if exists public.nutrition cascade;
drop table if exists public.friendships cascade;
drop table if exists public.challenge_participants cascade;
drop table if exists public.challenges cascade;
drop table if exists public.messages cascade;
drop table if exists public.progress_entries cascade;
drop table if exists public.notifications cascade;
drop table if exists public.device_tokens cascade;
drop table if exists public.user_xp cascade;
drop table if exists public.user_health_profiles cascade;
drop table if exists public.profiles cascade;

-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- ==========================================
-- 1. PROFILE & HEALTH SYSTEMS
-- ==========================================

-- User Profiles (Auth-linked)
create table if not exists public.profiles (
    id uuid references auth.users on delete cascade primary key,
    username text unique not null,
    avatar_url text,
    is_premium boolean default false not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- User Health Profiles (Calorie & Step Targets)
create table if not exists public.user_health_profiles (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    height_cm real,
    weight_kg real,
    age integer,
    daily_step_goal integer default 8000 not null,
    daily_calorie_goal integer default 2000 not null,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_user_health unique(user_id)
);

-- Firebase FCM Device Tokens
create table if not exists public.device_tokens (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    fcm_token text unique not null,
    device_type text default 'android' not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- ==========================================
-- 2. GAMIFICATION (XP SYSTEM)
-- ==========================================

create table if not exists public.user_xp (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    xp_amount integer not null,
    source text not null, -- 'step_goal', 'workout_complete', 'meal_logged', 'meditation'
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- ==========================================
-- 3. FITNESS ENGINE
-- ==========================================

-- Exercise Database
create table if not exists public.exercises (
    id uuid default uuid_generate_v4() primary key,
    name text not null,
    category text not null, -- 'cardio', 'strength', 'yoga', 'stretching'
    description text,
    gif_url text
);

-- Workouts Logged
create table if not exists public.workouts (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    name text not null,
    duration_seconds integer not null,
    calories_burned integer not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Workout Exercises Join Table
create table if not exists public.workout_exercises (
    id uuid default uuid_generate_v4() primary key,
    workout_id uuid references public.workouts(id) on delete cascade not null,
    exercise_id uuid references public.exercises(id) on delete cascade not null,
    order_index integer not null
);

-- Exercise Sets (Reps, Weight, Durations)
create table if not exists public.exercise_sets (
    id uuid default uuid_generate_v4() primary key,
    workout_exercise_id uuid references public.workout_exercises(id) on delete cascade not null,
    reps integer,
    weight_kg real,
    duration_seconds integer,
    is_completed boolean default false not null
);

-- ==========================================
-- 4. PHYSICAL ACTIVITY STREAM
-- ==========================================

-- Daily Step Counts
create table if not exists public.step_counts (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    date date default current_date not null,
    steps integer default 0 not null,
    calories integer default 0 not null,
    distance_meters real default 0.0 not null,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_user_step_date unique(user_id, date)
);

-- Sleep Records
create table if not exists public.sleep_records (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    start_time timestamp with time zone not null,
    end_time timestamp with time zone not null,
    quality_score integer check (quality_score >= 1 and quality_score <= 100),
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- ==========================================
-- 5. NUTRITION ENGINE
-- ==========================================

-- Food Database
create table if not exists public.food_items (
    id uuid default uuid_generate_v4() primary key,
    name text not null,
    calories_per_100g integer not null,
    protein_g_per_100g real default 0.0,
    carbs_g_per_100g real default 0.0,
    fat_g_per_100g real default 0.0,
    image_url text
);

-- Meal Logs
create table if not exists public.meals (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    name text not null, -- 'Breakfast', 'Lunch', 'Dinner', 'Snack'
    date date default current_date not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Meal Foods Join Table (Quantities per food item)
create table if not exists public.meal_foods (
    id uuid default uuid_generate_v4() primary key,
    meal_id uuid references public.meals(id) on delete cascade not null,
    food_item_id uuid references public.food_items(id) on delete cascade not null,
    serving_size_g real not null
);

-- Nutrition aggregate view helper table/log (for fast dashboard fetch)
create table if not exists public.nutrition (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    date date default current_date not null,
    total_calories integer default 0 not null,
    total_protein real default 0.0 not null,
    total_carbs real default 0.0 not null,
    total_fat real default 0.0 not null,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_user_nutrition_date unique(user_id, date)
);

-- ==========================================
-- 6. SOCIAL ENGINE & CHALLENGES
-- ==========================================

-- Friends Table
create table if not exists public.friendships (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    friend_id uuid references public.profiles(id) on delete cascade not null,
    status text default 'pending' not null, -- 'pending', 'accepted', 'blocked'
    created_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_friendship_pair unique(user_id, friend_id)
);

-- Challenges
create table if not exists public.challenges (
    id uuid default uuid_generate_v4() primary key,
    title text not null,
    description text not null,
    start_date date not null,
    end_date date not null,
    target_metric text not null, -- 'steps', 'workouts'
    target_value integer not null,
    reward_xp integer not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Challenge Participants
create table if not exists public.challenge_participants (
    id uuid default uuid_generate_v4() primary key,
    challenge_id uuid references public.challenges(id) on delete cascade not null,
    user_id uuid references public.profiles(id) on delete cascade not null,
    current_progress integer default 0 not null,
    has_completed boolean default false not null,
    joined_at timestamp with time zone default timezone('utc'::text, now()) not null,
    constraint unique_participant unique(challenge_id, user_id)
);

-- Instant Messages
create table if not exists public.messages (
    id uuid default uuid_generate_v4() primary key,
    sender_id uuid references public.profiles(id) on delete cascade not null,
    receiver_id uuid references public.profiles(id) on delete cascade not null,
    content text not null,
    is_read boolean default false not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- ==========================================
-- 7. PROGRESS & NOTIFICATIONS
-- ==========================================

-- Visual progress / body stats logs
create table if not exists public.progress_entries (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    weight_kg real,
    body_fat_percentage real,
    photo_url text,
    recorded_at date default current_date not null
);

-- General Push/App Notifications log
create table if not exists public.notifications (
    id uuid default uuid_generate_v4() primary key,
    user_id uuid references public.profiles(id) on delete cascade not null,
    title text not null,
    body text not null,
    is_read boolean default false not null,
    created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- ==========================================
-- 8. SECURITY (ROW LEVEL SECURITY - RLS)
-- ==========================================

alter table public.profiles enable row level security;
alter table public.user_health_profiles enable row level security;
alter table public.device_tokens enable row level security;
alter table public.user_xp enable row level security;
alter table public.workouts enable row level security;
alter table public.step_counts enable row level security;
alter table public.sleep_records enable row level security;
alter table public.meals enable row level security;
alter table public.nutrition enable row level security;
alter table public.friendships enable row level security;
alter table public.challenge_participants enable row level security;
alter table public.messages enable row level security;
alter table public.progress_entries enable row level security;
alter table public.notifications enable row level security;

-- Policies for profile access
-- Policies for profile access
drop policy if exists "Profiles viewable by everyone" on public.profiles;
create policy "Profiles viewable by everyone" on public.profiles for select using (true);

drop policy if exists "Users can update own profile" on public.profiles;
create policy "Users can update own profile" on public.profiles for update using (auth.uid() = id);

-- General dynamic policies matching owner auth.uid
drop policy if exists "Health profiles managed by owner" on public.user_health_profiles;
create policy "Health profiles managed by owner" on public.user_health_profiles for all using (auth.uid() = user_id);

drop policy if exists "Device tokens managed by owner" on public.device_tokens;
create policy "Device tokens managed by owner" on public.device_tokens for all using (auth.uid() = user_id);

drop policy if exists "XP logs readable by owner" on public.user_xp;
create policy "XP logs readable by owner" on public.user_xp for select using (auth.uid() = user_id);

drop policy if exists "Workouts managed by owner" on public.workouts;
create policy "Workouts managed by owner" on public.workouts for all using (auth.uid() = user_id);

drop policy if exists "Steps managed by owner" on public.step_counts;
create policy "Steps managed by owner" on public.step_counts for all using (auth.uid() = user_id);

drop policy if exists "Sleep managed by owner" on public.sleep_records;
create policy "Sleep managed by owner" on public.sleep_records for all using (auth.uid() = user_id);

drop policy if exists "Meals managed by owner" on public.meals;
create policy "Meals managed by owner" on public.meals for all using (auth.uid() = user_id);

drop policy if exists "Nutrition managed by owner" on public.nutrition;
create policy "Nutrition managed by owner" on public.nutrition for all using (auth.uid() = user_id);

drop policy if exists "Friendships managed by owner" on public.friendships;
create policy "Friendships managed by owner" on public.friendships for all using (auth.uid() = user_id or auth.uid() = friend_id);

drop policy if exists "Challenge participation managed by owner" on public.challenge_participants;
create policy "Challenge participation managed by owner" on public.challenge_participants for all using (auth.uid() = user_id);

drop policy if exists "Messages managed by sender or receiver" on public.messages;
create policy "Messages managed by sender or receiver" on public.messages for all using (auth.uid() = sender_id or auth.uid() = receiver_id);

drop policy if exists "Progress entries managed by owner" on public.progress_entries;
create policy "Progress entries managed by owner" on public.progress_entries for all using (auth.uid() = user_id);

drop policy if exists "Notifications managed by owner" on public.notifications;
create policy "Notifications managed by owner" on public.notifications for all using (auth.uid() = user_id);

-- ==========================================
-- 9. TRIGGERS & FUNCTIONS
-- ==========================================

-- Trigger to automatically create a profile and standard health profile when a new user signs up in auth.users
create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, username, avatar_url, is_premium)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'username', split_part(new.email, '@', 1)),
    coalesce(new.raw_user_meta_data->>'avatar_url', 'https://ik.imagekit.io/default_avatar.png'),
    false
  );
  
  insert into public.user_health_profiles (user_id, daily_step_goal, daily_calorie_goal)
  values (new.id, 8000, 2000);
  
  return new;
end;
$$ language plpgsql security definer;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

