## Github Link ## - https://github.com/UnarineBusisiwe/HolyTextApplication

## YouTube ## - https://youtube.com/watch?v=NWK1Z_u3nIU&feature=shared

## HolyText — A Sanctuary for your soul.
## HolyText  is a family-friendly Bible study and engagement Android app built in Kotlin. It combines Bible Reader, Search Bible, Bookmarks and Highlights, Study and Explore, and Streaks to encourage consistent Bible engagement.


## Purpose
HolyText was designed to solve a common problem: most Bible apps offer either deep study tools or engagement mechanics, but rarely both in a single, family-friendly experience.

## The app provides: 
- A single, trusted Translation (KJV for the prototype, and NIV for the POE)
- Study Features include Reading Plans, Audio bible, Bible Trivia and bible Word Search
- Engagement features — Faith Points, streaks, and a daily verse.
- Multilingual support — English (active), Tshivenda and Sepedi (planned for the final PoE).

## Planned for the final PoE
- Offline mode with Room database
- Push notifications via Firebase Cloud Messaging
- Full Tshivenda / Sepedi translations
- Bible Trivia game
- Word Search
- Community reflections
- Google Single Sign-On

##  REST API Integration
The Bible Reader and Daily Verse screens call the free "bible-api.com" REST service via Retrofit and  OkHttp and JSON.

## AI Usage Statement

## Overview
During the development of HolyText, I used AI tools — primarily **ChatGPT** — to assist with specific technical and documentation tasks. All AI-generated content was reviewed, tested on a physical device, and adjusted to fit this project's requirements before inclusion in the final submission.

## Areas Where AI Was Used

## Code Generation and Structure
- Generating the initial scaffolding of Firebase Authentication flows (`RegisterActivity`, `LoginActivity`) including input validation and error messages.
- Structuring the Retrofit client (`BibleApi.kt`) to connect the Bible Reader to the `bible-api.com` REST service.
- Creating the pure-Kotlin `Validators` object so validation logic could be unit tested without Android framework dependencies.

## Debugging
AI was used to interpret Android Logcat stack traces and Gradle build errors, including:
- A "MalformedJsonException" caused by sending spaces instead of `+` in the Bible API query string.
- A "NullPointerException" caused by calling "findViewById(R.id.toolbar)" on a layout without a toolbar.
- A JVM-target mismatch between Kotlin (21) and Java (17), resolved using "kotlin { jvmToolchain(17) }".
- Enabling AndroidX support in Gradle.

## Documentation
AI assisted with structuring this README, the AI Usage Statement, and the demo video script. All content was rewritten and customised to reflect HolyText specifically.

## Testing
AI provided example test cases for "ValidatorsTest.kt" covering email format, password rules, and chapter-number validation. The assertions were verified by running the tests locally and confirming all nine passed before submission.

## Areas Where AI Was NOT Used
AI was not used for:
- Choosing the app's purpose, name, or visual identity.
- Deciding which features to implement (based on Part 1 research into Bible App for Kids, YouVersion Bible, and Bible Note: Sermon Notes).
- Designing the Firestore schema or REST API endpoints.
- Writing the UI layouts ('activity_main', 'activity_register', 'activity_home', 'activity_settings', 'activity_bible_reader', 'activity_daily_verse', 'activity_bookmarks').

## Verification and Accountability
Every AI-assisted code snippet was:
1. Read line by line to confirm it matched the project's structure and requirements.
2. Tested on an Android device.
3. Logged using 'Log.d()' and 'Log.e()' calls so behaviour could be verified in Logcat.
4. Adjusted where necessary to align with the app's design decisions.

The unit tests, CI workflow, and demonstration video provide independent verification that the final application functions as intended.

## HolyText — A Sanctuary for your soul.
## HolyText  is a family-friendly Bible study and engagement Android app built in Kotlin. It combines Bible Reader, Search Bible, Bookmarks and Highlights, Study and Explore, and Streaks to encourage consistent Bible engagement.


## Purpose
HolyText was designed to solve a common problem: most Bible apps offer either deep study tools or engagement mechanics, but rarely both in a single, family-friendly experience.

## The app provides: 
- A single, trusted Translation (KJV for the prototype, and NIV for the POE)
- Study Features include Reading Plans, Audio bible, Bible Trivia and bible Word Search
- Engagement features — Faith Points, streaks, and a daily verse.
- Multilingual support — English (active), Tshivenda and Sepedi (planned for the final PoE).

## Planned for the final PoE
- Offline mode with Room database
- Push notifications via Firebase Cloud Messaging
- Full Tshivenda / Sepedi translations
- Bible Trivia game
- Word Search
- Community reflections
- Google Single Sign-On

##  REST API Integration
The Bible Reader and Daily Verse screens call the free "bible-api.com" REST service via Retrofit and  OkHttp and JSON.

## AI Usage Statement

## Overview
During the development of HolyText, I used AI tools — primarily **ChatGPT** — to assist with specific technical and documentation tasks. All AI-generated content was reviewed, tested on a physical device, and adjusted to fit this project's requirements before inclusion in the final submission.

## Areas Where AI Was Used

## Code Generation and Structure
- Generating the initial scaffolding of Firebase Authentication flows (`RegisterActivity`, `LoginActivity`) including input validation and error messages.
- Structuring the Retrofit client (`BibleApi.kt`) to connect the Bible Reader to the `bible-api.com` REST service.
- Creating the pure-Kotlin `Validators` object so validation logic could be unit tested without Android framework dependencies.

## Debugging
AI was used to interpret Android Logcat stack traces and Gradle build errors, including:
- A "MalformedJsonException" caused by sending spaces instead of `+` in the Bible API query string.
- A "NullPointerException" caused by calling "findViewById(R.id.toolbar)" on a layout without a toolbar.
- A JVM-target mismatch between Kotlin (21) and Java (17), resolved using "kotlin { jvmToolchain(17) }".
- Enabling AndroidX support in Gradle.

## Documentation
AI assisted with structuring this README, the AI Usage Statement, and the demo video script. All content was rewritten and customised to reflect HolyText specifically.

## Testing
AI provided example test cases for "ValidatorsTest.kt" covering email format, password rules, and chapter-number validation. The assertions were verified by running the tests locally and confirming all nine passed before submission.

## Areas Where AI Was NOT Used
AI was not used for:
- Choosing the app's purpose, name, or visual identity.
- Deciding which features to implement (based on Part 1 research into Bible App for Kids, YouVersion Bible, and Bible Note: Sermon Notes).
- Designing the Firestore schema or REST API endpoints.
- Writing the UI layouts ('activity_main', 'activity_register', 'activity_home', 'activity_settings', 'activity_bible_reader', 'activity_daily_verse', 'activity_bookmarks').

## Verification and Accountability
Every AI-assisted code snippet was:
1. Read line by line to confirm it matched the project's structure and requirements.
2. Tested on an Android device.
3. Logged using 'Log.d()' and 'Log.e()' calls so behaviour could be verified in Logcat.
4. Adjusted where necessary to align with the app's design decisions.

The unit tests, CI workflow, and demonstration video provide independent verification that the final application functions as intended.
