<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/6d346c15-4894-49f4-b274-4d793cefa40c

## Run Locally

**Prerequisites:**  Node.js


1. Install dependencies:
   `npm install`
2. Set the `GEMINI_API_KEY` in [.env.local](.env.local) to your Gemini API key
3. Run the app:
   `npm run dev`

## Firebase production configuration

This customer app is configured for the `chutki-production` Firebase project. See [FIREBASE_CHUTKI_PRODUCTION_SETUP.md](FIREBASE_CHUTKI_PRODUCTION_SETUP.md) for the exact Android package, Realtime Database endpoint, required Console checks, and the separate Web App registration requirement for browser/PWA deployments.
