# Layered Time — Android app

This is a native Android project for the Layered Time app.

## Location behavior
- Requests `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` at runtime.
- Uses GPS/network location while the app is visible.
- Displays the actual latitude/longitude and accuracy.
- Places a glowing marker on the Earth graphic.
- Does not request background location because the current feature only needs location while the app is open.

## Build
Open this folder in Android Studio and let Gradle sync. Build > Build APK(s).

The project targets Android API 36 and has minimum Android API 23.
