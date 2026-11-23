# Chutki Customer App – backend update

## Applied
- Android applicationId: `com.chutki.user` (matches registered Firebase Android app)
- Source namespace retained as `com.chutki.customer` to preserve existing Kotlin source packages
- Firebase project changed to `chutki-production`
- Realtime Database URL changed to the Singapore production database
- Supplied `google-services.json` installed
- Supabase project URL and publishable key configured in BuildConfig
- OkHttp and Firebase coroutine token dependency added
- `SupabaseRestClient.kt` added; requests use Firebase ID token plus Supabase publishable key

## Not yet verified / migrated
- This archive has not been compiled or run on a physical Android device here.
- Supabase Third-Party Auth must accept Firebase tokens.
- Firebase token claims must satisfy the Supabase JWT and RLS configuration.
- Existing customer feature repositories and screens are not automatically migrated to Supabase by adding the transport class. Existing Firebase operations remain as written until each flow is migrated and tested against the actual Supabase schema.
- Do not assume cart/order/payment data is stored in Supabase until those calls are migrated and verified.

## Security
- Only the Supabase publishable key is included.
- No `sb_secret_` or `service_role` key is included.
- Rotate the secret key pasted into chat before production use.
