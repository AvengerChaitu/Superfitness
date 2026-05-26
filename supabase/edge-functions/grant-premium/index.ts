import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

serve(async (req) => {
  const { user_id } = await req.json()

  if (!user_id) {
    return new Response(JSON.stringify({ error: "user_id required" }), { status: 400 })
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  )

  // Call Razorpay API to create a payment link
  const razorpayRes = await fetch("https://api.razorpay.com/v1/payment_links", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Basic " + btoa(
        Deno.env.get("RAZORPAY_KEY_ID") + ":" + Deno.env.get("RAZORPAY_KEY_SECRET")
      )
    },
    body: JSON.stringify({
      amount: 49900,
      currency: "INR",
      description: "Thrivio Lifetime Premium",
      notes: { user_id },
      callback_url: "thrivio://payment-success",
      callback_method: "get"
    })
  })

  const link = await razorpayRes.json()

  if (!razorpayRes.ok) {
    return new Response(JSON.stringify({ error: link.error }), { status: 500 })
  }

  return new Response(JSON.stringify({ url: link.short_url, id: link.id }), {
    headers: { "Content-Type": "application/json" }
  })
})
