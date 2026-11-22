import { getApps, initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getDatabase } from 'firebase/database';
import { getMessaging, isSupported, type Messaging } from 'firebase/messaging';

// Firebase project: chutki-production
// Android app registered in Firebase: com.chutki.user
// NOTE: This app currently uses the Android Firebase App ID for its shared
// Capacitor/WebView runtime. Register a separate Firebase Web App in Console
// and replace appId below with that Web App's appId before deploying the PWA
// to ordinary browsers. Do not use a service-account key in this file.
export const firebaseConfig = {
  apiKey: 'AIzaSyBdEVEy3maol-2ieEnWb19ghw5w-j6rXPY',
  authDomain: 'chutki-production.firebaseapp.com',
  databaseURL: 'https://chutki-production-default-rtdb.asia-southeast1.firebasedatabase.app',
  projectId: 'chutki-production',
  storageBucket: 'chutki-production.firebasestorage.app',
  messagingSenderId: '66128890021',
  appId: '1:66128890021:android:c206602da6e92b163a254c',
};

const app = getApps().length > 0 ? getApps()[0] : initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getDatabase(app, firebaseConfig.databaseURL);

let messagingPromise: Promise<Messaging | null> | null = null;

export function getFirebaseMessagingSafe(): Promise<Messaging | null> {
  if (!messagingPromise) {
    messagingPromise = isSupported()
      .then((supported) => (supported ? getMessaging(app) : null))
      .catch(() => null);
  }
  return messagingPromise;
}
