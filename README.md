# 📱 Cashflow Pro – Budget Tracker App

## 📌 Overview

Cashflow Pro is a mobile budgeting application developed using **Kotlin** in Android Studio. The app helps users manage their finances by tracking expenses, setting budget goals, and analyzing spending patterns.

This project was developed as part of an academic assignment and focuses on **functionality, data persistence using RoomDB, and a user-friendly interface**.

---

## 🎯 Features

### 🔐 User Authentication

* Login with username and password
* Register new user account

### 🏠 Dashboard (Home Screen)

* Displays financial overview
* Shows total balance and remaining budget
* Quick navigation to all features

### 💸 Expense Management

* Add expense with:

  * Amount
  * Category
  * Date
  * Start & End time
  * Description
* Optional receipt photo upload

### 📂 Categories

* Create, edit, and delete categories
* Organize expenses effectively

### 🎯 Budget Settings

* Set minimum and maximum monthly spending goals
* Visual indicators (SeekBar / progress tracking)

### 📜 History

* View all expenses
* Filter by selected date range

### 📊 Reports

* View total spending per category
* Helps understand spending habits

### 📈 Analytics

* Graphs and charts for spending trends

### 🏆 Rewards

* Earn badges for consistent tracking
* Encourages good financial habits

---

## 🧭 Navigation Flow

```
Login → Dashboard
        ↓
   Add Expense
        ↓
   View History → Reports → Analytics
        ↓
        Rewards
```

---

## 🛠️ Technologies Used

* Kotlin
* Android Studio
* Room Database (SQLite)
* XML Layouts
* Intents (Navigation)
* RecyclerView
* SeekBar
* Camera API (for receipt images)

---

## 🗄️ Data Storage

All data is stored locally using **RoomDB**, including:

* Users
* Expenses
* Categories
* Budget settings

---

## 🧪 Testing

Basic testing was conducted on:

* Expense creation
* Data retrieval from RoomDB
* Navigation between activities

Automated build testing is implemented using **GitHub Actions**.

---

## ⚙️ Installation & Setup

1. Clone the repository:

```
git clone https://github.com/your-username/cashflow-pro.git
```

2. Open in Android Studio

3. Build the project:

```
Build → Make Project
```

4. Run the app on:

* Emulator OR
* Physical Android device

---

## 📦 APK

The APK file is included in the repository under:

```
/app/build/outputs/apk/
```

---

## 🎥 Demo Video

Watch the full demonstration here:
👉 **[Add your YouTube link here]**

The video includes:

* App walkthrough
* Feature demonstration
* Voice-over explanation

---

## 📝 Code Quality

* Well-commented Kotlin code
* Logging implemented using `Log.d()`
* Input validation to prevent crashes

---

## 📂 Project Structure

```
com.example.cashflowpro
│
├── activities
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── DashboardActivity
│   ├── AddExpenseActivity
│   ├── CategoryActivity
│   ├── BudgetActivity
│ 
│   ├── ReportsActivity
│   ├── AnalyticsActivity
│   └── RewardsActivity
│
├── data
│   ├── Expense.kt
│   ├── Category.kt
│   ├── Budget.kt
│   ├── DAO files
│   └── AppDatabase.kt
```

---

## ⚠️ Notes

* The app is a prototype and may contain minor bugs
* Designed for offline use (local database only)
* Focused on demonstrating Android development concepts

---

## 👨‍💻 Author

**Clarity Masuku*St10438928*

**Sibusiso Mabena*ST10462532* 

**Uanthi Mgandela*ST10447100*

IT Student – Software Development

---

## 📄 License

This project is for educational purposes only.


## Video presetantion
https://youtu.be/d7gGaG1Sz48
