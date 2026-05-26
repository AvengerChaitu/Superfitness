-- ====================================================================
-- THRIVIO RLS POLICIES
-- Run after schema.sql in Supabase SQL Editor
-- ====================================================================

-- Enable RLS on all tables
alter table public.user_health_profiles enable row level security;
alter table public.user_xp enable row level security;
alter table public.step_counts enable row level security;
alter table public.sleep_records enable row level security;
alter table public.nutrition enable row level security;
alter table public.workouts enable row level security;
alter table public.workout_exercises enable row level security;
alter table public.exercise_sets enable row level security;
alter table public.exercises enable row level security;
alter table public.meals enable row level security;
alter table public.meal_foods enable row level security;
alter table public.food_items enable row level security;
alter table public.progress_entries enable row level security;
alter table public.notifications enable row level security;
alter table public.device_tokens enable row level security;
alter table public.challenges enable row level security;
alter table public.challenge_participants enable row level security;
alter table public.friendships enable row level security;
alter table public.messages enable row level security;

-- User Health Profiles: users can read/update only their own
create policy "Users can view own health profile"
  on public.user_health_profiles for select
  using (auth.uid() = user_id);

create policy "Users can update own health profile"
  on public.user_health_profiles for update
  using (auth.uid() = user_id);

create policy "System can insert on signup"
  on public.user_health_profiles for insert
  with check (auth.uid() = user_id);

-- XP: users can read all (leaderboard), insert/update only own
create policy "Anyone can read XP (leaderboard)"
  on public.user_xp for select
  using (true);

create policy "Users can insert own XP"
  on public.user_xp for insert
  with check (auth.uid() = user_id);

create policy "Users can update own XP"
  on public.user_xp for update
  using (auth.uid() = user_id);

-- Step counts: users manage their own
create policy "Users can read own steps"
  on public.step_counts for select
  using (auth.uid() = user_id);

create policy "Users can insert own steps"
  on public.step_counts for insert
  with check (auth.uid() = user_id);

create policy "Users can update own steps"
  on public.step_counts for update
  using (auth.uid() = user_id);

-- Sleep records: user-private
create policy "Users can read own sleep"
  on public.sleep_records for select
  using (auth.uid() = user_id);

create policy "Users can manage own sleep"
  on public.sleep_records for insert
  with check (auth.uid() = user_id);

create policy "Users can update own sleep"
  on public.sleep_records for update
  using (auth.uid() = user_id);

-- Nutrition: private
create policy "Users can read own nutrition"
  on public.nutrition for select
  using (auth.uid() = user_id);

create policy "Users can insert own nutrition"
  on public.nutrition for insert
  with check (auth.uid() = user_id);

create policy "Users can update own nutrition"
  on public.nutrition for update
  using (auth.uid() = user_id);

-- Workouts: private
create policy "Users can read own workouts"
  on public.workouts for select
  using (auth.uid() = user_id);

create policy "Users can manage own workouts"
  on public.workouts for insert
  with check (auth.uid() = user_id);

create policy "Users can update own workouts"
  on public.workouts for update
  using (auth.uid() = user_id);

-- Exercises: read-only shared library
create policy "Anyone can read exercises"
  on public.exercises for select
  using (true);

-- Workout exercises: own
create policy "Users can read own workout exercises"
  on public.workout_exercises for select
  using (
    exists (select 1 from public.workouts w where w.id = workout_id and w.user_id = auth.uid())
  );

create policy "Users can insert own workout exercises"
  on public.workout_exercises for insert
  with check (
    exists (select 1 from public.workouts w where w.id = workout_id and w.user_id = auth.uid())
  );

-- Exercise sets: own
create policy "Users can read own exercise sets"
  on public.exercise_sets for select
  using (
    exists (select 1 from public.workout_exercises we
      join public.workouts w on w.id = we.workout_id
      where we.id = workout_exercise_id and w.user_id = auth.uid())
  );

create policy "Users can insert own exercise sets"
  on public.exercise_sets for insert
  with check (
    exists (select 1 from public.workout_exercises we
      join public.workouts w on w.id = we.workout_id
      where we.id = workout_exercise_id and w.user_id = auth.uid())
  );

-- Meals (log entries): private
create policy "Users can read own meals"
  on public.meals for select
  using (auth.uid() = user_id);

create policy "Users can manage own meals"
  on public.meals for insert
  with check (auth.uid() = user_id);

-- Food items: read-only shared library
create policy "Anyone can read food items"
  on public.food_items for select
  using (true);

-- Meal foods: own
create policy "Users can read own meal foods"
  on public.meal_foods for select
  using (
    exists (select 1 from public.meals m where m.id = meal_id and m.user_id = auth.uid())
  );

create policy "Users can insert own meal foods"
  on public.meal_foods for insert
  with check (
    exists (select 1 from public.meals m where m.id = meal_id and m.user_id = auth.uid())
  );

-- Progress entries: private
create policy "Users can read own progress"
  on public.progress_entries for select
  using (auth.uid() = user_id);

create policy "Users can manage own progress"
  on public.progress_entries for insert
  with check (auth.uid() = user_id);

-- Notifications: own
create policy "Users can read own notifications"
  on public.notifications for select
  using (auth.uid() = user_id);

-- Device tokens: own
create policy "Users can manage own device tokens"
  on public.device_tokens for all
  using (auth.uid() = user_id);

-- Challenges: publicly readable
create policy "Anyone can read challenges"
  on public.challenges for select
  using (true);

create policy "Admins can manage challenges"
  on public.challenges for all
  using (exists (select 1 from auth.users where id = auth.uid() and raw_user_meta_data ->> 'role' = 'admin'));

-- Challenge participants: own entries
create policy "Anyone can read challenge participants"
  on public.challenge_participants for select
  using (true);

create policy "Users can join challenges"
  on public.challenge_participants for insert
  with check (auth.uid() = user_id);

create policy "Users can update own challenge progress"
  on public.challenge_participants for update
  using (auth.uid() = user_id);

-- Friendships: involved users
create policy "Users can read own friendships"
  on public.friendships for select
  using (auth.uid() = user_id or auth.uid() = friend_id);

create policy "Users can send friend requests"
  on public.friendships for insert
  with check (auth.uid() = user_id);

create policy "Users can update received requests"
  on public.friendships for update
  using (auth.uid() = friend_id);

-- Messages: involved users
create policy "Users can read own messages"
  on public.messages for select
  using (auth.uid() = sender_id or auth.uid() = receiver_id);

create policy "Users can send messages"
  on public.messages for insert
  with check (auth.uid() = sender_id);
