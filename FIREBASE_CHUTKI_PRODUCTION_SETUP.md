# Chutki Firebase production project integration

## Applied in this ZIP
- Firebase project ID: `chutki-production`
- Realtime Database: `https://chutki-production-default-rtdb.asia-southeast1.firebasedatabase.app`
- Android applicationId / Firebase registered package: `com.chutki.user`
- Firebase Android App ID: `1:66128890021:android:c206602da6e92b163a254c`
- Capacitor appId aligned to `com.chutki.user`
- Firebase Authentication remains Email/Password + Google only. Phone sign-in is not configured. The signup phone field remains a customer contact/profile field, not an authentication provider.
- Existing UI, order, wallet, chat, call, and tracking code was not intentionally redesigned.

## Firebase Console checks required
1. Authentication → Sign-in method: enable Email/Password and Google; keep Phone disabled.
2. Realtime Database → Rules: replace the current open test rules with reviewed production rules before allowing customers to use the app. Do not set global `.read` or `.write` to `true`.
3. Add the Android SHA-1 and SHA-256 fingerprints for the signing certificate(s) used to build the app, then re-download `google-services.json` if Firebase updates the OAuth clients.
4. The supplied `google-services.json` belongs to the Android app registration `com.chutki.user` and is copied to `android-kotlin-app/app/google-services.json`.
5. For a browser/PWA deployment, register a separate **Web app** in Firebase Project Settings and replace the `appId` in `src/firebase.ts` with the Web app's `appId`. The supplied JSON only proves the Android app registration; it does not contain a Web app registration.

## Important security limitation
The existing Realtime Database rules file is preserved and has not been deployed or rewritten by this integration. Review it before publishing: client-side role flags and wallet/order financial mutations must not be trusted as authorization. Admin privileges must be assigned only by a trusted admin process, never by a customer client.

## Secret handling
Do not put `FIREBASE_SERVICE_ACCOUNT_JSON`, service-account private keys, Supabase secret keys, or other server secrets in this ZIP's Android assets, frontend source, or `google-services.json`. Keep server credentials in Supabase Edge Function Secrets only.
