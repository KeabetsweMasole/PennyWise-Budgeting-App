1.Project name: OPSC_POE

Name of app 
-
Pennywise


Author:
-
Masole Keabetswe  

Role:
-
Project Manager

Author:
-
Matlhaga Amogelang 

Role:
-
Lead Developer

Author: 
-
Mathe Lesego 

Role:
-
Sacretary

2.Overview 

-This system helps user to manage personal finances by monitoring income, categorizing daily expenses, and setting realistic spending limits. It eliminates manual spreadsheet calculations, reduces financial stress, and provides clear insights to help you build better spending habits and achieve your savings goals.

User guide 

Step 1:
-
-To begin using the Pennywise Budget Management Application, the user must open the application on their device. Once the application starts, a splash screen displaying the Pennywise logo appears briefly before the main dashboard loads. This screen confirms that the application is launching successfully.

-For instance wen a user taps the Pennywise application icon, the splash screen appears and then automatically directs them to the Home page.

Step 2:
-
-After launching the application, the user is taken to the Home Dashboard. This page provides a summary of their financial information, including their available balance, income, expenses, minimum budget, maximum budget, and recent transactions. The dashboard allows users to quickly monitor their financial status without navigating through multiple pages.

-If a user has an income of R10,000 and total expenses of R1,500, the dashboard will show that R8,500 remains available.

Step 3:
-
-To create a budget plan, the user selects the Budget section from the navigation menu. The application allows the user to enter a starting balance, minimum budget amount, and maximum budget amount. Once the information has been entered, the user clicks the “Save Budget Goals” button to store the budget settings.

-For instance, a user may set a starting balance of R10,000, a minimum budget of R500, and a maximum budget of R10,000. These values will then be displayed on the Home Dashboard.

Step 4:
-
-Application allows users to record their spending by selecting the Add Expense option. The user enters details such as the amount spent, description of the item, category, date, and time. Once the information is saved, the expense is automatically added to the transaction history and deducted from the available balance.

-For instance,a user purchases a gaming controller for R1,500. They enter "Gaming Stuff" as the category and "Controller" as the description before saving the transaction.

Step 5:
-
-All recorded expenses are displayed in the Recent Transactions section on the Home Dashboard. This feature allows users to review their latest spending activities and keep track of where their money is being spent.

-for instance, after recording the gaming controller purchase, the transaction appears in the Recent Transactions list with the amount of R1,500.

Step 6:
-
-Application provides a receipt feature that allows users to view detailed information about a specific transaction. By selecting the Receipt button next to a transaction, the user can access information such as the expense category, amount, description, date, and time.

-For instance, selecting the receipt for the gaming controller purchase displays details showing that R1,500 was spent on a controller under the Gaming Stuff category.

Step 7:
-
-Visuals section provides graphical representations of spending data. The application generates charts that help users understand how their expenses are distributed across different categories. This feature makes it easier to identify spending patterns and areas where costs may be reduced.

-pie chart may show that Food expenses account for the largest portion of monthly spending, followed by Transport, Entertainment, and Utilities.

Step 8:
-
-Users can generate reports for a specific period by selecting a start date and an end date. After applying the filter, the application displays only the transactions recorded within the selected timeframe. This feature is useful for reviewing monthly or weekly spending.

-If a user selects the period from 1 April 2026 to 30 April 2026, only expenses recorded during April 2026 will appear in the report.

Step 9:
-
-application allows users to download their financial reports for record-keeping and analysis. By selecting the Download All option, a report containing transaction and budget information is generated and saved for future reference.

-user may download a complete monthly spending report to review their expenses or share the information with a financial advisor.

Step 10:
-
-Once the user has completed their activities, they can safely exit the application by selecting the Logout or Power button located at the top of the screen. This ensures that personal financial information remains secure.

-For instance after checking expenses and reviewing reports, the user clicks the Logout button to end their session and protect their account.

 Technologies Used
 -

| Category | Technology | Purpose |

| **Language**| **Kotlin** | Modern, type-safe language for Android development. |
| **Storage** | **SQLite** | Local persistence utilizing **Atomic Transactions** to ensure XP and expenses update simultaneously. |
| **UI Engine** | **Material Design 3** | Professional-grade buttons, inputs, and navigation components. |
| **Charts**| **MPAndroidChart** | Rendering complex, high-density PieCharts with interactive labels. |
| **Libraries** | **androidx.core-ktx** | Streamlined Android APIs and ActivityResultContracts for secure file handling. |
| **Formatting** | **DecimalFormat** | Specialized currency rendering (Space thousand-separator, comma decimal). |

---

  Key Features
  -

Real-Time Dashboard: View your "In My Pocket" balance, total income, and total expenses at a glance.
Expense Validation: A built-in logic engine that prevents users from logging an expense if it exceeds their current balance.
Persistent Receipt Management: Uses `takePersistableUriPermission` to ensure receipt images remain viewable even after device reboots.
Professional Reporting:Interactive Pie Charts with outside-slice labels and high-contrast Card View layouts for transaction history.
Gamification: Earn 10 XP for every transaction logged, encouraging disciplined financial tracking.
Localized Currency:** Standardized South African formatting (e.g.,R 10 000,00) used across all screens.

 The Build Process
 -

The development followed a structured Software Development Lifecycle (SDLC)**:

1.  Requirement Analysis: Identified the need for a tracker that acts as a "financial safety net" by preventing over-spending.
2.  Database Design: Built a robust SQLite schema to handle expenses, categories, dates, and file paths for receipts.
3.  UI/UX Overhaul: Migrated to a "Deep Blue" professional theme with elevated Material 3 cards for improved readability.
4.  Security & Permissions: Implemented a modern "Open Document" intent system to handle receipt images securely within scoped storage requirements.
5.  Validation Logic: Integrated real-time checks between **FinancePrefs** (Income) and the Database (Total Expenses).





