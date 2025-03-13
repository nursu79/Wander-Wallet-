# Wander-Wallet-

Project Name: Wander Wallet
Description:
Wander Wallet is a travel expense and budget management application designed for travelers who want to track their spending efficiently. It helps users set budgets for their trips, categorize expenses, and generate reports on their spending habits. This application ensures that users can plan their finances effectively while traveling, avoiding overspending and maintaining financial discipline.

Core Functionalities & Business Logic
1. Authentication & Authorization
  User Registration & Login: Users can sign up and log in using a secure authentication mechanism (e.g., JWT-based authentication).
  Role-based Access: Implement different user roles (e.g., Admin, Regular User) to control access to different features.

2. Business Feature 1: Travel Budget Planning
  💼 Functionality:
  
  Users can create a new travel plan with the following details:
  
  Destination
  Start & End Dates
  Estimated Budget
  Currency Selection
  Users can edit, delete, or view their trips.
  
  Budget comparison: Users can see a real-time comparison between their estimated budget and actual spending.
  
  📌 Business Logic:
  
  When a user creates a new trip, a new entry is stored in the database containing trip details.
  Each trip has a budget linked to it, which is referenced when adding expenses.
  Users receive alerts if they exceed their planned budget.

3. Business Feature 2: Expense Tracking & Categorization
  📊 Functionality:
  
  Users can add expenses to a trip under different categories (e.g., Food, Transport, Accommodation, Activities).
  Users can attach receipts or notes to each expense entry.
  Users can view daily, weekly, and overall expense reports in the form of lists and charts.
  Users can search and filter expenses based on date, category, or amount.
  🛠 Business Logic:
  
  When an expense is added, it is linked to a specific trip and stored in the database.
  The system dynamically updates the remaining budget and alerts the user if expenses exceed the allocated amount.
  Users can edit or delete expenses, ensuring accurate record-keeping.


  | Names                         |     ID      |
| ----------------------------- | ----------- |
| Saba Habtamu                  | UGR/7546/15 |
| Sumeya Ibrahim                | UGR/6702/15 |
| Yabtesega Kinfe               | UGR/2887/15 |
| Yunus Kedir                   | UGR/8173/13 |




  

