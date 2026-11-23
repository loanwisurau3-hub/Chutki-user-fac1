# Chutki Customer – temporary chutki-api /me test

This update adds a temporary button to the customer home screen. When tapped, it obtains the current signed-in Firebase user's ID token in memory and sends an HTTPS GET request to:

`https://aqtfvlsbxgpctlmaffr.supabase.co/functions/v1/chutki-api/me`

The token is sent only as `Authorization: Bearer ...`; it is not logged, displayed, or written to disk. The screen displays only the HTTP status and API response body. Remove the temporary button and test method after verification.

## Test steps
1. Build and install this updated Android project.
2. Sign in to Chutki Customer using Firebase Authentication.
3. On the home screen tap **Test Firebase Login + API /me**.
4. Expected success: HTTP 200 with `ok: true`, Firebase UID, profile and roles. A 401 means token verification/configuration still needs investigation; a 403/account error may indicate missing or inactive `profiles` row. A 500 indicates a server-side function/config/schema issue; inspect Supabase Edge Function logs.

Do not copy Firebase ID tokens into chat or screenshots. The response may contain account profile data; share screenshots only after hiding personal fields if present.

## Scope and limitations
- This is a temporary connectivity/authentication test only.
- It does not migrate catalog, cart, orders, payment, or other app workflows from Firebase to Supabase.
- This archive has not been compiled or device-tested in this environment. Build it in Android Studio/GitHub Actions and report any compiler error before installing.
