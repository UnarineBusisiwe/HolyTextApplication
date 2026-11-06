## Github Link ## - https://github.com/UnarineBusisiwe/HolyTextApplication

## YouTube ## - https://youtube.com/watch?v=2GtP8GTKnl0&feature=shared

## HolyText — A Sanctuary for your soul.
## HolyText  is a family-friendly Bible study and engagement Android app built in Kotlin. It combines Bible Reader, Search Bible, Bookmarks and Highlights, Study and Explore, and Streaks to encourage consistent Bible engagement.

##Project Overview

HolyText is a native Android application developed using Kotlin in Android Studio. The application aims to support users in their personal Bible-study journeys by providing access to Scripture, language options and interactive learning activities in one mobile experience.

The application includes account registration and login, a home screen, a Bible reader, Bible trivia, a Faith Points reward mechanism, daily activity streak tracking and personal settings. Firebase services are used for authentication and cloud storage of user profile information.

##Purpose and Problem Statement

Purpose

The purpose of HolyText is to provide a convenient and engaging mobile platform through which users can read Bible passages, explore Scripture in supported languages and reinforce their biblical knowledge through interactive activities.

Problem being addressed

Some users may find it difficult to maintain a consistent Bible-reading routine or may prefer to engage with Scripture in a familiar language. A reading-only experience may also offer limited interaction or motivation to return regularly.

HolyText addresses these needs by bringing Bible reading, multilingual access, trivia and progress-based encouragement together in one application.

##Project objectives

• Provide a mobile interface for reading Bible chapters.
• Support English, Sepedi and Tshivenda language options.
• Allow users to create accounts and securely authenticate through Firebase Authentication.
• Store user profile and selected preferences using Cloud Firestore.
• Encourage regular engagement through Faith Points and activity streaks.
• Reinforce biblical knowledge through multiple-choice trivia and explanations.
• Allow users to personalise selected reading and application settings.

##Target Users

HolyText is intended for:

• Individuals who want to read and study the Bible using a mobile device.
• Users who prefer English, Sepedi or Tshivenda language options.
• Young people and other users who enjoy interactive learning.
• Individuals who want encouragement to build a regular Bible-reading habit.

The application is designed for Android devices running Android 7.0 (API level 24) or later, subject to device compatibility.

##Main Features
Feature    and Description  

Registration     |Users can create an account with their name, email address and password. Input validation checks required fields and matching passwords.
Login            |Users can sign in using email and password through Firebase Authentication.                                                             
Google Sign-In   |The login screen includes Google authentication using Android Credential Manager and Firebase Authentication.                           
Home screen      |Displays a welcome message and the user’s Faith Points and activity streak when profile data is available.                              
Bible reader     |Users select a Bible book and chapter. The application retrieves passage data through a REST API.                                       
Language options |The application provides English, Sepedi and Tshivenda options through its language helper.                                             
Bible trivia     |Users answer multiple-choice Bible questions and receive feedback and explanations.                                                     
Faith Points     |Users can earn points for successful Bible-reading and trivia activities.                                                               
Activity streak  |The application checks the user’s last active date and updates the consecutive-day activity count when appropriate.                     
Personal settings|Users can select a language, choose a text size and enable or disable the notification preference.                                      
Logout           |Users can sign out of their Firebase account.                                                                                           

##Innovative Features Introduced

The following features make the current version more engaging than a basic Bible-reading application. They are implemented in the supplied Part 3 source code.

##Multilingual Bible experience

HolyText includes English, Sepedi and Tshivenda language options. The BibleLanguageHelper class supports language-specific Bible book names, references and verse text handling. This improves accessibility for users who are more comfortable engaging with Scripture in one of the supported languages.

##Faith Points reward system

The application introduces a points-based encouragement mechanism. Users can earn Faith Points through supported activities, including successfully loading a Bible passage and answering trivia questions. The points are associated with the user’s profile in Cloud Firestore.

##Daily activity streak tracking

HolyText records a user’s last active date and streak value. When the user returns on a different date, the application checks the stored date and updates the streak. This feature is intended to encourage consistency in Bible study.

##Interactive Bible trivia

The trivia feature turns biblical learning into an interactive activity. It includes multiple-choice answers, question progress, scoring, feedback and explanations. Questions and answer options are provided in the supported languages.

##Google authentication

In addition to email-and-password login, users can access the Google Sign-In option. This provides an alternative authentication route and reduces the need to enter credentials manually each time an account is created or accessed.

##Personalised reading preferences

Users can choose a preferred language and Bible text size. The selected preferences are saved locally using Android SharedPreferences and are also synchronised with the user’s Firestore profile when the user is signed in and the update succeeds.

##Design Considerations

The design and development of HolyText considered the following factors:

##Usability and navigation

The application uses separate screens for registration, login, the home page, Bible reading, trivia and settings. This separates the main tasks and helps users navigate the application without placing every function on one screen.

##Accessibility and language

Language selection supports users who prefer English, Sepedi or Tshivenda. Text-size settings provide Small, Medium and Large options to make Bible passages more comfortable to read.

##User experience and engagement

Faith Points, trivia feedback and daily streaks introduce interactive elements that encourage continued use. Progress indicators help users understand their position within a trivia session.

##Authentication and data protection

Firebase Authentication manages account sign-in and registration. User profile information and selected settings are stored in Cloud Firestore under the authenticated user’s unique identifier. Input validation is performed before registration and login requests are submitted.

Firebase security rules must be configured to ensure that users can only access or modify data they are authorised to use. Authentication alone does not replace appropriate Firestore security rules.

##Performance and connectivity

The Bible reader retrieves passage information from an external REST API. A loading indicator is displayed while a passage is being retrieved, and the application provides feedback when a request fails or no passage is returned. An internet connection is therefore required for API-based Bible passage retrieval.

##Maintainability

The project separates some responsibilities into individual Kotlin classes. For example, BibleApi handles the API service definition, BibleLanguageHelper handles language-related processing and Validators contains reusable input-validation functions. This makes the code easier to understand, test and maintain.

##Application Architecture and Data

HolyText is organised as an Android application with activity-based screens and supporting helper classes.

Main application components

• MainActivity: application entry screen.
• RegisterActivity: account registration and input validation.
• LoginActivity: email/password and Google sign-in.
• HomeActivity: user welcome information, Faith Points and streak display.
• BibleReaderActivity: Bible book and chapter selection and passage display.
• TriviaActivity: interactive Bible quiz, scoring and feedback.
• SettingsActivity: language, text-size and notification preferences.
• BibleApi: Retrofit service and Bible API response models.
• BibleLanguageHelper: language-related Bible book and verse processing.
• Validators: reusable validation functions.

Firebase data

The supplied implementation uses a Firestore collection named users. A user document is identified using the Firebase Authentication user ID. Profile fields used by the application include:

• name
• email
• preferredLanguage
• notificationsEnabled
• streak
• faithPoints
• lastActiveDate
• createdAt
• textSize (when a text-size preference is synchronised)

The application also uses Android SharedPreferences under holytext_prefs to retain local language, text-size and notification preferences.

Bible API

The Bible reader uses Retrofit to request a passage from the configured Bible API service. The response is mapped into Kotlin data models and displayed in the reader. The language helper processes the language-specific book names, references and verse text.

##GitHub and Version Control

GitHub is used as the central repository for storing and managing the HolyText source code.

How GitHub is used

1. Repository hosting: The Android Studio project is maintained in a GitHub repository so that the source code is accessible to authorised project contributors.
2. Version control: Git tracks changes to the project files over time.
3. Commit history: Contributors can commit changes with messages describing the work completed.
4. Collaboration: Team members can share code, review updates and contribute to the same project.
5. Backup and access: The remote repository provides a shared copy of the project’s source code.

##Release Notes

Release notes document the development progress from the original prototype to the current version. The entries below describe functionality confirmed in the supplied source code. The exact prototype-to-final comparison must be checked against the original prototype repository or project files before submission.

Version 1.0.0 – Part 3 final development

Release status: Part 3 documentation/build candidate
Platform: Android
Development language: Kotlin

Features included in the supplied version

• Added or included Firebase Authentication for email-and-password account registration and login.
• Included Google Sign-In through Credential Manager and Firebase Authentication.
• Integrated Cloud Firestore for user profile information and selected preferences.
• Implemented a Bible reader using Retrofit and a REST API.
• Added English, Sepedi and Tshivenda language handling.
• Implemented Bible trivia with multiple-choice answers, question progress, scoring and explanations.
• Added the Faith Points reward mechanism for supported reading and trivia activities.
• Implemented daily activity streak tracking using the user’s last active date.
• Added user settings for language, text size and notification preference.
• Added reusable validation functions and JUnit tests for email, password and chapter validation.

Innovative features highlighted in this release

1. Multilingual Bible reading support for English, Sepedi and Tshivenda.
2. Faith Points to encourage participation in Bible reading and learning activities.
3. Daily streak tracking to encourage consistent engagement.
4. Interactive Bible trivia with scoring and explanations.
5. Google Sign-In as an alternative authentication method.
6. Personalised language and text-size preferences.

##Improvements and technical work

• Organised application functionality into separate activities and helper classes.
• Added asynchronous API retrieval and loading/error feedback in the Bible reader.
• Added local preference storage and Firestore preference synchronisation.
• Added unit tests for reusable validation logic.
• Prepared an automated GitHub Actions workflow for build and test checks (once the workflow file is committed and successfully executed).

## Known Limitations and Future Improvements

The following are potential areas for further development. They should not be interpreted as features already completed:

• Expand the Bible content and verify the accuracy and consistency of all supported-language translations.
• Add offline Bible reading and local passage storage.
• Expand the trivia question bank and consider additional difficulty levels.
• Implement and test actual notification delivery if notifications are part of the intended user experience.
• Improve error handling and synchronisation when the device has an unstable internet connection.
• Add further automated UI and integration tests.
• Review Firestore security rules and test access control.
• Consider additional accessibility improvements.

The unit tests, CI workflow, and demonstration video provide independent verification that the final application functions as intended.

## AI Tools Usage and Acknowledgement

Artificial Intelligence (AI) tools were used as supporting resources during the development and documentation of the HolyText Android application. ChatGPT (OpenAI) was used to assist with understanding programming concepts, troubleshooting technical issues and improving project documentation.

The following are examples of how AI assistance was utilised:

## Code assistance: ChatGPT was used to explain Kotlin code, Android development concepts and Firebase integration, particularly the interaction between Firebase Authentication and Cloud Firestore.
## Debugging and troubleshooting: AI assistance was used to investigate errors and understand possible causes of database connection and data-storage issues, including the difference between authenticating users through Google Sign-In and creating their corresponding Firestore user profiles.

## Technical guidance: ChatGPT provided explanations and suggested approaches for implementing and understanding application functionality, including user authentication, database operations, API integration and application testing.

## Documentation: ChatGPT assisted in structuring and improving the GitHub README file, explaining the application’s purpose, features, design considerations, technologies used, GitHub version control and GitHub Actions.
## Testing guidance: AI assistance was used to identify possible test scenarios and provide guidance on verifying application functionality.

AI-generated suggestions and code examples were treated as supporting references rather than automatically accepted solutions. The project code and recommendations were reviewed and considered in relation to the existing application implementation.

The development team remains responsible for understanding the application, making implementation decisions, testing functionality and ensuring that the submitted work accurately represents the completed project.

AI tool acknowledgement:

OpenAI. (2026). ChatGPT [Generative AI tool]. https://chatgpt.com/
