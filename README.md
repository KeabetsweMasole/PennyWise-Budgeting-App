## 💰 Pennywise: Personal Finance Tracker

**Pennywise** is a professional-grade Android application designed to help users take full control of their financial lives. Unlike basic expense trackers, Pennywise provides a localized experience with **robust SQLite-based data persistence**, real-time budget validation, and a sophisticated reporting engine.

The app allows users to set an "In My Pocket" budget, track daily expenses across various categories, and manage digital receipts through a secure photo-upload system. It also incorporates **gamification elements**, such as an XP (Experience Points) system, to encourage consistent financial logging.

---

## 🛠 Technologies Used

| Category | Technology | Purpose |

| **Language** | **Kotlin** | Modern, type-safe language for Android development. |
| **Storage** | **SQLite** | Local persistence utilizing **Atomic Transactions** to ensure XP and expenses update simultaneously. |
| **UI Engine** | **Material Design 3** | Professional-grade buttons, inputs, and navigation components. |
| **Charts** | **MPAndroidChart** | Rendering complex, high-density PieCharts with interactive labels. |
| **Libraries** | **androidx.core-ktx** | Streamlined Android APIs and ActivityResultContracts for secure file handling. |
| **Formatting** | **DecimalFormat** | Specialized currency rendering (Space thousand-separator, comma decimal). |

---

## ✨ Key Features

* **Real-Time Dashboard:** View your "In My Pocket" balance, total income, and total expenses at a glance.
* **Expense Validation:** A built-in logic engine that prevents users from logging an expense if it exceeds their current balance.
* **Persistent Receipt Management:** Uses `takePersistableUriPermission` to ensure receipt images remain viewable even after device reboots.
* **Professional Reporting:** Interactive Pie Charts with outside-slice labels and high-contrast **Card View** layouts for transaction history.
* **Gamification:** Earn **10 XP** for every transaction logged, encouraging disciplined financial tracking.
* **Localized Currency:** Standardized South African formatting (e.g., **R 10 000,00**) used across all screens.

---

## ⌨️ Keyboard & Navigation Shortcuts

While designed for touch-screen Android devices, the following shortcuts apply when using an emulator or physical keyboard:

* **Enter Key:** Submits login and expense forms quickly.
* **Tab Key:** Navigates between input fields (**Amount -> Description -> Date**).
* **Back Button:** Always returns to the previous screen or the Dashboard.
* **Bottom Nav:** Quick-switch between Home, Add Expense, Progress, and Reports.

---

## 🏗 The Build Process

The development followed a structured **Software Development Lifecycle (SDLC)**:

1.  **Requirement Analysis:** Identified the need for a tracker that acts as a "financial safety net" by preventing over-spending.
2.  **Database Design:** Built a robust SQLite schema to handle expenses, categories, dates, and file paths for receipts.
3.  **UI/UX Overhaul:** Migrated to a **"Deep Blue"** professional theme with elevated Material 3 cards for improved readability.
4.  **Security & Permissions:** Implemented a modern "Open Document" intent system to handle receipt images securely within scoped storage requirements.
5.  **Validation Logic:** Integrated real-time checks between **FinancePrefs** (Income) and the Database (Total Expenses).

---

## 🎓 Key Learnings

* **Atomic Transactions:** Gained experience in wrapping database operations in transactions to ensure that if an expense log fails, the XP reward isn't granted erroneously, maintaining data integrity.
* **Persistent Permissions:** Mastered handling Android URIs so that receipt images remain accessible even after system cache clears.
* **Data Visualization:** Solved label collision issues in **MPAndroidChart** when displaying high-density data across multiple spending categories.
* **Resource Management:** Understood the importance of using **SharedPreferences** for quick settings and **SQLite** for heavy transaction history.

---

## 🚀 Future Improvements

* **Cloud Synchronization:** Integrating Firebase or AWS to allow users to sync their data across multiple devices.
* **Biometric Security:** Adding Fingerprint or FaceID locks to protect sensitive financial data.
* **Export to CSV/PDF:** Allowing users to export their transaction history for tax purposes or external accounting.
* **AI Insights:** Implementing a machine learning module to predict future spending based on historical habits.

---

**Author:** Masole Keabetswe  
**Role:** Project Manager
<br>

**Author:** Matlhaga Amogelang  
**Role:** Lead Developer
<br>

**Author:** Mathe Lesego  
**Role:** Sacretary
<br>

**Date:** April 2026
