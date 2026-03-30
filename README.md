# Mess Feedback System

A Java-based CLI application to gather hostel mess feedback from students and provide automatic analysis, trends, and alerts for mess management.

# Problem Statement

In a typical college hostel mess, feedback from students is collected either on paper, which is then ignored, or via WhatsApp groups, which is then forgotten. There is no efficient method to:
- Determine which meals are consistently rated low by students
- Detect a decline in the quality of food over a series of days
- Determine what the most common complaints are from the feedback provided by the students

This application addresses this problem by allowing for the efficient collection of feedback and automatically highlighting trends for the mess management to act on.

# Features
- Student Can submit feedback for any meal (Breakfast / Lunch / Snacks / Dinner)
- Student Can rate meal on scale of 1 to 5
- Student Can write comments on experience
- Student Can view history of submitted feedback

# Admin / Mess Manager
- Can view all feedback submitted
- Can view all feedback for specific meal types
- Can view analysis report for all feedback with:
  - Average ratings for each meal type (in stars too)
  - Overall average rating
  - What meal type performed best/worst
  - What are top keywords for all comments
  - What are top complaint keywords for all low-rated feedback
- Can export analysis report to .txt file
- Can view active alerts (meals with poor ratings)

## How to Run

```bash
# Compile
javac -d bin src/edu/ccrm/cli/MainMenu.java src/edu/ccrm/cli/CLIApp.java src/edu/ccrm/domain/*.java

# Run
java -cp bin edu.ccrm.cli.CLIApp
```

## Sample Run

```
====================================
      Mess Feedback System
====================================
  1. Student
  2. Admin / Mess Manager
  3. Exit
====================================
Choose role: 2

Enter admin password: admin123

--- Admin Menu ---
1. View all feedback
2. View feedback by meal type
3. Generate analysis report
4. Export report to file
5. View active alerts
6. Week-over-week comparison
7. Back

Choice: 3

========================================
     MESS FEEDBACK ANALYSIS REPORT
========================================
Total feedbacks  : 182
Overall average  : 3.21 / 5
Best meal        : DINNER
Worst meal       : LUNCH

--- Average rating per meal ---
  BREAKFAST     ★★★★☆  3.72/5
  LUNCH         ★★☆☆☆  2.03/5
  SNACKS        ★★★★☆  3.89/5
  DINNER        ★★★★☆  3.91/5

--- Top keywords in all comments ---
  cold            28 times
  stale           19 times
  watery          16 times
  crispy          14 times
  hot             13 times
  undercooked     12 times
  tasty           11 times
  fresh           10 times

--- Active alerts ---
  !! CRITICAL !! LUNCH rating dropped to 1.67/5 on 2026-03-13
  ! WARNING !  LUNCH rating dropped to 2.33/5 on 2026-03-17
========================================
```

---

## Default Credentials

| Role | Credential |
| Student | Any student ID (e.g. `24BCE1001`) |
| Admin | Password: `admin123` |

You can change the admin password in `Main.java` line:
```java
if (!password.equals("admin123"))
```

## Sample Dataset

A sample dataset for March 2026 with 182 feedback records from students with IDs like `24BAI`, `24BCE`, `24BCS`, `24BME` is included in `data/feedback.csv`.
## Java Concepts Used

| Concept | Where |
| OOP and encapsulation | All model classes |
| Enum with methods | `MealType.java` |
| Comparable interface | `Feedback.java` |
| ArrayList and HashMap | `FeedbackService`, `AnalysisService` |
| File I/O | `FileStorage.java` |
| Exception handling | All service and storage classes |
| LocalDate and Date API | `Feedback`, `Alert`, `AlertService` |
| Bubble sort (manual) | `FeedbackService`, `AnalysisService` |
| StringBuilder | `AnalysisService.generateReport()` |
| Static nested enum | `Alert.Severity` |

## Author

- Name: Akash Kumar Pandit
- ID: 24BAI10629
- Course: Programming in Java
- Institution: VIT Bhopal University
