import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const RAZORPAY_WEBHOOK_SECRET = Deno.env.get("RAZORPAY_WEBHOOK_SECRET") || ""

interface RazorpayWebhookPayload {
  event: string
  payload: {
    payment: {
      entity: {
        id: string
        amount: number
        status: string
        notes: {
          user_id: string
        }
      }
    }
  }
}

serve(async (req) => {
  const body: RazorpayWebhookPayload = await req.json()

  if (body.event !== "payment.captured") {
    return new Response(JSON.stringify({ ok: true }), { status: 200 })
  }

  const payment = body.payload.payment.entity
  const userId = payment.notes?.user_id

  if (!userId || payment.amount < 49900) {
    return new Response(JSON.stringify({ error: "Invalid payment" }), { status: 400 })
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  )

  const { error } = await supabase
    .from("user_health_profiles")
    .update({ is_premium: true, premium_updated_at: new Date().toISOString() })
    .eq("user_id", userId)

  if (error) {
    console.error("Failed to grant premium:", error)
    return new Response(JSON.stringify({ error: error.message }), { status: 500 })
  }

  return new Response(JSON.stringify({ ok: true }), { status: 200 })
})
