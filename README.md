# Cashflow Pro - Smart Personal Budget Tracker

**Cashflow Pro** is a premium, offline-first Android application designed for **young professionals and students** who want to take full control of their personal finances. Built with Kotlin and modern Android Jetpack components, it simplifies daily transaction tracking and motivates users through intelligent budgeting and gamified progress.

## 🚀 Overview
Cashflow Pro provides a secure and intuitive environment to manage your money. It focuses on privacy, visual clarity, and encouraging financial discipline. Whether you're tracking daily coffee expenses or saving for a long-term milestone, Cashflow Pro serves as your ultimate financial companion.

## 🎨 Design Decisions
*   **Fintech Clean Aesthetics**: We adopted a "Premium Fintech" style using a neutral palette (Slate & White) with **Emerald Green** (#0FBA83) as the primary action color to evoke a sense of growth and success.
*   **Information Architecture**: A "One-Tap" navigation philosophy was used, placing major features (Expenses, Analytics, Goals) within easy reach via a Bottom Navigation bar and a central Floating Action Button for quick entries.
*   **Visual Feedback**: Instead of just showing numbers, we used dynamic progress indicators and "Health Status" cards to give users an immediate emotional connection to their financial data.
*   **Premium Motion**: Implemented sophisticated entrance animations and a subtle floating logo effect on the splash screen to create a high-end startup feel from the first second.

## 🛠️ Custom Features
### 1. Visual Spending Health Monitor
Unlike standard trackers, Cashflow Pro uses a triple-state logic to monitor your "Financial Health." Based on your set minimum and maximum targets, the app dynamically changes the UI state:
*   **🟡 Below Minimum**: Alerts you when you haven't reached your savings/spending floor.
*   **🟢 Within Goal**: A "Sweet Spot" indicator that confirms you are on track.
*   **🔴 Over Budget**: Immediate visual warning when spending exceeds your defined safety limit.

### 2. Gamified Rewards & Achievement System
To combat "budgeting fatigue," we integrated a rewards engine that tracks user behavior. Users earn XP and unlock badges (🥉, 🥈, 🥇) for maintaining streaks, reaching 50% of a goal, or staying under budget for a week. This turns a chore into a rewarding experience.

## 🎥 Demo Video
[Watch the Cashflow Pro Walkthrough](https://youtu.be/placeholder) *(Replace with actual link)*

## 📸 Screenshots
| Welcome | Dashboard | Expenses |
| :---: | :---: | :---: |
| ![Welcome](/screenshots/welcome.png) | ![Dashboard](/screenshots/dashboard.png) | ![Expenses](/screenshots/expenses.png) |

| Analytics | Goals | Rewards |
| :---: | :---: | :---: |
| ![Analytics](/screenshots/analytics.png) | ![Goals](/screenshots/goals.png) | ![Rewards](/screenshots/rewards.png) |

## 💻 GitHub & CI/CD
*   **Version Control**: We utilized a Git-flow inspired workflow, using branch management to isolate feature development (e.g., `feature/navigation-fix`) from the `master` branch.
*   **GitHub Actions**: We implemented an automated CI/CD pipeline. On every push to `master`, a GitHub Action:
    1.  Sets up the Java/Android environment.
    2.  Runs Lint checks for code quality.
    3.  Executes unit and instrumented tests.
    4.  Builds the debug APK automatically for rapid distribution.

## 🛠️ Technologies Used
*   **Kotlin**: The primary language for modern Android development.
*   **Room Database**: Local data persistence for high-speed offline access.
*   **Jetpack Navigation**: Seamless fragment-based app flow.
*   **MPAndroidChart**: Powerful, interactive data visualization library.
*   **Biometric API**: Industry-standard secure login integration (Fingerprint/Face).
*   **jBCrypt**: Secure cryptographic password hashing.
*   **Coroutines**: Efficient background processing.
*   **Material Design 3**: Modern, responsive UI components.

## 🏗️ Architecture
The project follows a modular **MVC (Model-View-Controller)** pattern, leveraging Android Jetpack components to ensure lifecycle awareness.

```text
CashflowPro/
├── .github/workflows/    # CI/CD Automation (GitHub Actions)
├── app/
│   ├── src/main/java/com/example/cashflowpro/
│   │   ├── data/         # Room Database, Entities, and DAOs
│   │   ├── presentation/ # Fragments and UI Logic
│   │   └── Activities    # Main UI Controllers
│   ├── src/main/res/     # Layouts, Themes, and Drawables
│   └── build.gradle.kts  # Dependencies
```

## ⚙️ Installation & Build
1.  **Clone**: `git clone https://github.com/jBlack-MC/CashflowPro.git`
2.  **Open**: Open the project in **Android Studio Ladybug**.
3.  **Sync**: Allow Gradle to sync dependencies.
4.  **Run**: Deploy to an emulator or physical device (API 24+).

## 🚀 What's New
### v1.2 Release Notes (Latest)
*   **Navigation Fix**: Resolved critical `ClassCastException` in Login screens.
*   **UI/UX**: Added premium floating logo animation on Splash screen.
*   **New Feature**: Full Expense Editing and Swipe-to-Delete support.
*   **New Feature**: Savings Goal management with fund addition and deletion.
*   **Gamification Engine**: XP tracking and Achievement Badges are now fully functional and linked to real user data (transaction counts, goal progress, and income levels).

## 👨‍💻 Authors
Developed with ❤️ by the **Cashflow Pro Team**.

 **Sibusiso Mabena*ST10462532* 

**Clarity Masuku*ST10438928*

**Uanthi Mgandela*ST10447100*

## 📄 License
This project is licensed under the **MIT License**.
