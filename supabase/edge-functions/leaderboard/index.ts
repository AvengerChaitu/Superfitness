import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

serve(async (req) => {
  const url = new URL(req.url)
  const period = url.searchParams.get("period") || "weekly"
  const limit = parseInt(url.searchParams.get("limit") || "50")

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  )

  const since = period === "weekly"
    ? new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString()
    : period === "monthly"
    ? new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString()
    : new Date(0).toISOString()

  const { data, error } = await supabase
    .from("user_xp")
    .select("user_id, total_xp, level, username")
    .gte("updated_at", since)
    .order("total_xp", { ascending: false })
    .limit(limit)

  if (error) {
    return new Response(JSON.stringify({ error: error.message }), { status: 500 })
  }

  const ranked = data.map((row, i) => ({ rank: i + 1, ...row }))

  return new Response(JSON.stringify(ranked), {
    headers: { "Content-Type": "application/json" }
  })
})
