# Smart Pantry Manager

## 1. Application Description

**Smart Pantry Manager** is a Java-based Android application designed to help users manage their household pantry and reduce food waste.

The application allows users to keep track of ingredients they currently have at home by recording ingredient names, quantities, and measurement units. Based on the available pantry ingredients, the application recommends recipes that the user can prepare without purchasing additional ingredients.

The main feature of the application is its **strict recipe-matching system**. A recipe is only displayed under Suggested Recipes when every required ingredient is available in the pantry in a sufficient quantity. Recipes with missing ingredients or insufficient quantities are excluded.

### Main Features

- **Pantry Management:** Add, view, edit, and delete pantry ingredients.
- **Ingredient Tracking:** Store ingredient names, quantities, and measurement units.
- **Recipe Collection:** Access a collection of 15 preloaded recipes.
- **Suggested Recipes:** View recipes that can be prepared using the ingredients currently available.
- **Strict Ingredient Matching:** Only suggest recipes when all required ingredients and quantities are available.
- **Persistent Storage:** Save pantry and recipe information locally using SQLite.

## 2. Database Choice and Justification

### SQLite Database

The application uses **SQLite** as its database management solution.

SQLite was selected because it is lightweight, reliable, and integrated into the Android platform. It allows the application to store and retrieve information locally without requiring an external database server or an active internet connection.

The database is managed through Android's SQLite database functionality using the `SQLiteOpenHelper` class.

### Why SQLite Was Chosen

- **Local Storage:** Pantry ingredients and recipe information are stored directly on the device.
- **No Internet Required:** The application can access its locally stored data without an internet connection.
- **Android Compatibility:** SQLite is supported natively by Android.
- **Persistent Data:** Information remains available after the application is closed and reopened.
- **Simple Implementation:** SQLite is suitable for an application with a relatively small amount of structured data.

### Database Structure

The application uses three main database tables:

| Table | Description |
|---|---|
| `ingredients` | Stores pantry ingredient information, including names, quantities, and units. |
| `recipes` | Stores recipe names and preparation instructions. |
| `recipe_ingredients` | Stores the ingredients and quantities required for individual recipes. |

The database is created and managed through the `DatabaseHelper` Java class.

## 3. Requirements and Prerequisites

Before running the application, ensure that the following software is installed:

- **Android Studio:** Used to open, build, and run the project.
- **Java Development Kit (JDK):** Required for compiling the Java application. Use the JDK version supported by the project's Gradle configuration.
- **Android SDK:** Install the SDK platform and build tools required by the project.
- **Android Emulator or Physical Android Device:** Used to execute and test the application.
- **Git:** Required if cloning the project from GitHub.

## 4. Installation and Setup Instructions

### Step 1: Clone the Repository

Open a terminal or command prompt and navigate to the directory where you want to store the project.

Run:

```bash
git clone https://github.com/Tshamase/SmartPantryMananger
```


Navigate into the downloaded project directory:

```bash
cd SmartPantryManager
```

Alternatively, download the repository as a ZIP file from GitHub and extract it.

### Step 2: Open the Project in Android Studio

1. Launch Android Studio.
2. Select **File → Open**.
3. Navigate to the extracted or cloned Smart Pantry Manager project folder.
4. Select the project folder containing the Gradle project files.
5. Click **Open**.
6. Allow Android Studio to load the project.

### Step 3: Synchronise Gradle

After opening the project:

1. Wait for Android Studio to finish loading.
2. If prompted, select **Sync Now**.
3. Alternatively, select **File → Sync Project with Gradle Files**.
4. Wait for Gradle synchronisation and dependency resolution to finish.

If Android Studio requests additional Android SDK components, install the components required by the project.

### Step 4: Configure an Emulator or Android Device

**Option A: Android Emulator**

1. Open **Tools → Device Manager**.
2. Select **Create Virtual Device** if no emulator is available.
3. Choose an Android device model.
4. Select an installed Android system image compatible with the project.
5. Complete the setup and start the emulator.

**Option B: Physical Android Device**

1. Enable Developer Options on your Android device.
2. Enable USB Debugging.
3. Connect the device to your computer using a USB cable.
4. Accept the USB debugging permission prompt on the device.
5. Confirm that the device appears in Android Studio's device selector.

### Step 5: Run the Application

1. Select the `app` run configuration in Android Studio.
2. Select your preferred emulator or connected Android device.
3. Click the green **Run ▶** button.
4. Wait for Android Studio to build and install the application.
5. Once installation is complete, Smart Pantry Manager should launch on the selected device.

## 5. Running the Project Using the Command Line (Optional)

The application can also be built using Gradle from the project root directory.

**Windows:**

```bash
gradlew.bat assembleDebug
```

**Linux/macOS:**

```bash
./gradlew assembleDebug
```

To install the debug application on a connected Android device:

**Windows:**

```bash
gradlew.bat installDebug
```

**Linux/macOS:**

```bash
./gradlew installDebug
```

The generated debug APK can normally be found at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 6. Database Initialisation

On the first application launch, the SQLite database is created automatically through the application's database helper.

The database helper creates the required tables and inserts the preloaded recipe collection.

The application then allows users to add their own pantry ingredients. Pantry information is stored locally and remains available when the application is closed and reopened.

**Note:** The database is managed internally by the application. There is no need to install or configure a separate database server.

## 7. Using the Application

1. Launch Smart Pantry Manager.
2. Navigate to the pantry section.
3. Add ingredients by entering the ingredient name, quantity, and measurement unit.
4. View the saved ingredients in the pantry list.
5. Edit or delete ingredients when necessary.
6. Open the Suggested Recipes section.
7. View recipes that can be prepared using the currently available pantry ingredients.
8. Add a missing ingredient or update an insufficient quantity to see how the suggested recipe list changes.

A recipe will only appear when all its required ingredients are available in sufficient quantities.

## 8. Important Notes

- The application is developed using **Java** in Android Studio.
- The database uses **SQLite** for local data storage.
- The recipe collection is preloaded when the database is first created.
- No external database server is required.
- The project should be opened using Android Studio so that Gradle can configure the required build environment.

---

**Project:** Smart Pantry Manager  
**Module:** Mobile App Development 700  
**Platform:** Android  
**Programming Language:** Java  
**Database:** SQLite
