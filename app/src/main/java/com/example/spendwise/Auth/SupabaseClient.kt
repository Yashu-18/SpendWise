package com.example.spendwise.Auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://rzaebgywjdswbwmhrblw.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ6YWViZ3l3amRzd2J3bWhyYmx3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU4NTg3NDAsImV4cCI6MjA2MTQzNDc0MH0.VvlMA_zXLXZ9UO1jTYQxfDHXgMd6_NZaO-cjdRw0WVk"
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}
