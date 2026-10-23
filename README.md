# 🥘 Smart Pantry Manager

A Java Android Application that Suggests Recipes Based Strictly on Leftover Ingredients to Cut Food Waste.

## 📱 App Description
The Smart Pantry Manager is a mobile application designed to help users reduce household food waste. Users can track the ingredients they currently have at home (their "pantry") and the app will suggest recipes they can cook *right now*. 

The core value of this app is its **Strict-Matching Algorithm**: a recipe is only suggested if the user has 100% of the required ingredients in the required quantities. No shopping trips required, and no "almost there" suggestions to frustrate the user.

### ✨ Key Features
*   Pantry Management:Full CRUD (Create, Read, Update, Delete) functionality for tracking ingredients, quantities, units, and expiry dates.
*   Strict Recipe Matching: An intelligent algorithm that filters a database of 15+ pre-seeded recipes (South African, American, and Korean) against the user's live pantry.
*   Clean UI/UX: A modern "Porcelain" color scheme designed for high readability and WCAG accessibility compliance.
*   Persistent Storage: User data is saved locally and persists across app restarts.

## 🗄️ Database Choice & Justification
Chosen Database:SQLite (via `SQLiteOpenHelper`)

Why SQLite?
1.  Offline-First Architecture: A pantry tracker is a highly personal, local tool. Users shouldn't need an internet connection to check what they can cook for dinner. SQLite stores data locally on the device.
2.  Performance: SQLite is incredibly fast for local relational queries, which is essential for the strict-matching algorithm that needs to cross-reference the user's pantry against multiple recipe ingredients in real-time.
3.  No Backend Overhead: It eliminates the need for external servers, APIs, or authentication flows, keeping the app lightweight, secure, and strictly focused on the core business logic.

## 🛠️ Tech Stack
*   Language: Java
*   IDE: Android Studio
*   Database: SQLite
*   UI Components: RecyclerView, ConstraintLayout, BottomNavigationView, Material Design TextInputLayouts.

## 🚀 Setup and Run Instructions

### Prerequisites
*   Android Studio (Latest stable version recommended)
*   Java Development Kit (JDK) 11 or higher
*   An Android Emulator (API 24+) or a physical Android device with Developer Options enabled.

### How to Run
1.  Clone the Repository:
    ```bash
    git clone https://github.com/[YourUsername]/SmartPantryManager.git
    ```
2.  Open in Android Studio
    *   Open Android Studio.
    *   Select `File > Open` and navigate to the cloned `SmartPantryManager` folder.
    *   Allow Gradle to sync and download necessary dependencies.
3.  Run the App
    *   Connect your physical device or start an Android Emulator.
    *   Click the green **Run** ▶️ button in the top right corner of Android Studio.
4.  First Run Note:
    *   On the very first launch, the `DatabaseHelper` will automatically execute the `seedRecipes()` method, pre-loading 15 diverse recipes into the SQLite database.

---

## 🎓 Assignment Information
*   Module: Mobile App Development 700
*   Institution: Richfield Graduate Institute of Technology
*   Student Name: Tshamase Tshamase
*   Date: 23 October 2026
