package com.thrivio.network

import io.github.jan.tennert.supabase.createSupabaseClient
import io.github.jan.tennert.supabase.postgrest.Postgrest
import io.github.jan.tennert.supabase.gotrue.GoTrue
import io.github.jan.tennert.supabase.realtime.Realtime

object SupabaseClient {
    // Official Supabase credentials for Thrivio
    private const val SUPABASE_URL = "https://injjmyibdidnvnlzqhkf.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImluampteWliZGlkbnZubHpxaGtmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY4Njk4MDAsImV4cCI6MjA1MjQ0NTgwMH0.GbqNgJEgYpRs9w8k_6uR6fQCALFXz5MXYZxhnbVWCuo"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(GoTrue)
        install(Realtime)
    }
}
