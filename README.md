# Cashflow Pro - Smart Personal Budget Tracker

**Cashflow Pro** is a comprehensive, offline-first Android application designed to help users take full control of their personal finances. Built with Kotlin and modern Android Jetpack components, it combines daily transaction tracking with intelligent budgeting, savings goals, and gamified rewards to make financial management both effective and engaging.

## 🚀 Overview
Cashflow Pro provides a secure and intuitive environment to manage your money. It focuses on privacy, visual clarity, and motivating users to reach their financial milestones through interactive charts and achievement badges. Whether you're tracking daily coffee expenses or saving for a new car, Cashflow Pro is your ultimate financial companion.

## ✨ Key Features
*   **🔐 Secure Authentication**: Multi-layer security including BCrypt password hashing and Biometric Login (Fingerprint/Face Unlock).
*   **📊 Smart Dashboard**: Real-time summary of balance, total budget, and total spending with "Goal Range" visual health indicators.
*   **🎯 Savings Goals**: Dedicated tracker for large milestones. Monitor progress percentages and remaining amounts dynamically.
*   **📉 Interactive Analytics**: Visual breakdown of spending by category using high-performance Pie and Bar charts (MPAndroidChart).
*   **💸 Expense & Income Management**: Fast transaction logging with support for titles, amounts, custom categories, and notes.
*   **🏆 Gamification (Rewards)**: Stay motivated by unlocking badges like "Beginner Saver," "Smart Saver," and "Budget Master."
*   **📁 Custom Categories**: Full control over spending categories with custom emojis and descriptions.
*   **🔍 Advanced Search**: Instant filtering of transaction history by name or category.

## 📸 Screenshots
| Welcome | Dashboard | Expenses |
| :---: | :---: | :---: |
| ![Welcome](/screenshots/welcome.png) | ![Dashboard](/screenshots/dashboard.png) | ![Expenses](/screenshots/expenses.png) |

| Analytics | Goals | Rewards |
| :---: | :---: | :---: |
| ![Analytics](/screenshots/analytics.png) | ![Goals](/screenshots/goals.png) | ![Rewards](/screenshots/rewards.png) |

## 🛠️ Technologies Used
*   **Kotlin**: The primary language for modern Android development.
*   **Room Database**: Local data persistence for high-speed offline access.
*   **Jetpack Navigation**: Seamless fragment-based app flow.
*   **MPAndroidChart**: Powerful, interactive data visualization library.
*   **Biometric API**: Industry-standard secure login integration.
*   **jBCrypt**: Secure cryptographic password hashing.
*   **Coroutines & Lifecycle**: Efficient background processing and state management.
*   **Material Design 3**: Modern, responsive UI components.
*   **GitHub Actions**: Automated CI/CD pipeline for building and testing.

## 🏗️ Architecture
The project follows a modular structure focused on the **Model-View-Controller (MVC)** pattern, leveraging Android Jetpack components to ensure lifecycle awareness and robust data management.

```text
CashflowPro/
├── .github/workflows/    # CI/CD Automation
├── app/
│   ├── src/main/java/com/example/cashflowpro/
│   │   ├── data/         # Room Database, Entities, and DAOs
│   │   ├── presentation/ # Fragments and UI Logic
│   │   └── Activities    # Main UI Controllers
│   ├── src/androidTest/  # Automated Instrumented Tests
│   └── build.gradle.kts  # Project Dependencies
```

## ⚙️ Installation & Build
### Prerequisites
*   Android Studio Ladybug (2024.2.1) or newer.
*   Android SDK 24 (Nougat) or higher.

### Step-by-Step
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/username/CashflowPro.git
    ```
2.  **Open in Android Studio**:
    Select "Open an existing project" and navigate to the cloned folder.
3.  **Sync Gradle**:
    Allow Android Studio to download dependencies and sync the project structure.
4.  **Run the App**:
    Select a physical device or emulator (API 24+) and click **Run**.

### Building the APK
To generate a release APK:
1.  Navigate to `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
2.  The APK will be generated in `app/build/outputs/apk/debug/`.

## 🗄️ Database Structure
Cashflow Pro utilizes a relational Room database with the following key entities:
*   **User**: Handles local authentication and profile data.
*   **Expense / Income**: Stores transaction records linked to categories.
*   **Budget**: Defines user-set limits for monthly spending.
*   **SavingsGoal**: Tracks progress toward specific financial targets.
*   **Badge**: Manages unlocked achievements and rewards.

## 🧪 Automated Testing
The project includes a robust suite of instrumented tests to verify core logic:
*   **Database Operations**: Verifies CRUD operations for expenses and income.
*   **Financial Math**: Tests goal percentage calculations and budget remaining logic.
*   **Security**: Ensures biometric and hashed authentication flows work correctly.

Run tests via Gradle:
```bash
./gradlew connectedCheck
```

## 🚀 What's New
### v1.1 Release Notes
*   **New Feature**: Biometric Authentication (Fingerprint & Face Unlock).
*   **New Feature**: Advanced Search & Filtering in Transaction History.
*   **UI Improvement**: Sophisticated Entrance Animations for a premium onboarding experience.
*   **UI Improvement**: New "Visual Spending Health" indicators on the dashboard.
*   **UI Improvement**: Redesigned Category cards with emoji support.
*   **Bug Fix**: Resolved Jetifier deprecation warning in Gradle settings.
*   **Maintenance**: Fully automated CI/CD pipeline integrated via GitHub Actions.

## 🔮 Future Improvements
*   **Cloud Sync**: Integration with Firebase for multi-device data synchronization.
*   **Export Reports**: Support for exporting financial data to CSV or PDF.
*   **Shared Budgets**: Ability to manage shared finances with partners or family.
*   **Dark Mode**: Native support for system-wide dark theme.

## 👨‍💻 Authors
Developed with ❤️ by the **Cashflow Pro Team**.

## 📄 License
This project is licensed under the **MIT License**.
