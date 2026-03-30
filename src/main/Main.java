import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static FeedbackService feedbackService;
    static AnalysisService analysisService;
    static AlertService    alertService;
    static FileStorage     fileStorage;
    static Scanner         scanner;

    public static void main(String[] args) {
        scanner         = new Scanner(System.in);
        fileStorage     = new FileStorage();
        analysisService = new AnalysisService();
        alertService    = new AlertService();

        try {
            List<Feedback> existing = fileStorage.loadAll();
            feedbackService = new FeedbackService(existing);
            System.out.println("Loaded " + existing.size() + " existing feedback records.");
        } catch (IOException e) {
            feedbackService = new FeedbackService();
            System.out.println("No existing data found. Starting fresh.");
        }

        showMainMenu();
        scanner.close();
    }

    // MAIN MENU

    static void showMainMenu() {
        while (true) {
          
            System.out.println("---------Mess Feedback System-----------");
            System.out.println("1. Student                      ");
            System.out.println("2. Admin / Mess Manager         ");
            System.out.println("3. Exit                         ");

            System.out.print("Choose role: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": studentMenu(); break;
                case "2": adminMenu();   break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Enter 1, 2 or 3.");
            }
        }
    }


    // STUDENT MENU
    static void studentMenu() {
        System.out.print("\nEnter your Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (studentId.isEmpty()) {
            System.out.println("Student ID cannot be empty.");
            return;
        }

        while (true) {
            System.out.println("\n--- Student Menu [" + studentId + "] ---");
            System.out.println("1. Submit feedback");
            System.out.println("2. View my past feedback");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": submitFeedback(studentId); break;
                case "2": viewMyFeedback(studentId); break;
                case "3": return;
                default:  System.out.println("Invalid choice.");
            }
        }
    }

    static void submitFeedback(String studentId) {
        System.out.println("\nSelect meal type:");
        System.out.println("  1. Breakfast");
        System.out.println("  2. Lunch");
        System.out.println("  3. Snacks");
        System.out.println("  4. Dinner");
        System.out.print("Choice (1-4): ");

        MealType mealType;
        try {
            mealType = MealType.fromInput(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid meal type. Please enter 1 to 4.");
            return;
        }

        System.out.print("Rating (1-5): ");
        int rating;
        try {
            rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid rating. Please enter a number between 1 and 5.");
            return;
        }

        System.out.print("Comment: ");
        String comment = scanner.nextLine().trim();

        try {
            feedbackService.addFeedback(studentId, mealType, rating, comment);

            fileStorage.saveAll(feedbackService.getAllFeedback());
            System.out.println("\nFeedback submitted successfully. Thank you!");
            List<Feedback> todaysFeedback = feedbackService.getTodaysFeedback();
            Alert alert = alertService.checkImmediateAlert(todaysFeedback, mealType);
            if (alert != null) {
                System.out.println("\n[NOTICE] " + alert.getMessage());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Warning: Feedback recorded but could not be saved to file.");
        }
    }

    static void viewMyFeedback(String studentId) {
        List<Feedback> myFeedback = feedbackService.getByStudent(studentId);

        if (myFeedback.isEmpty()) {
            System.out.println("No feedback found for: " + studentId);
            return;
        }

        System.out.println("\n--- Your Feedback History ---");
        for (Feedback fb : myFeedback) {
            System.out.println("  " + fb);
        }
    }

    // ADMIN MENU

    static void adminMenu() {
        System.out.print("\nEnter admin password: ");
        String password = scanner.nextLine().trim();

        if (!password.equals("admin123")) {
            System.out.println("Incorrect password.");
            return;
        }

        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View all feedback");
            System.out.println("2. View feedback by meal type");
            System.out.println("3. Generate analysis report");
            System.out.println("4. Export report to file");
            System.out.println("5. View active alerts");
            System.out.println("6. Week-over-week comparison");
            System.out.println("7. Back");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": viewAllFeedback();  break;
                case "2": viewByMealType();   break;
                case "3": generateReport();   break;
                case "4": exportReport();     break;
                case "5": viewAlerts();       break;
                case "6": weeklyComparison(); break;
                case "7": return;
                default:  System.out.println("Invalid choice.");
            }
        }
    }

    static void viewAllFeedback() {
        List<Feedback> all = feedbackService.getAllFeedback();

        if (all.isEmpty()) {
            System.out.println("No feedback records found.");
            return;
        }

        System.out.println("\n--- All Feedback (" + all.size() + " records) ---");
        for (Feedback fb : all) {
            System.out.println("  " + fb);
        }
    }

    static void viewByMealType() {
        System.out.println("\nSelect meal type:");
        System.out.println("  1. Breakfast");
        System.out.println("  2. Lunch");
        System.out.println("  3. Snacks");
        System.out.println("  4. Dinner");
        System.out.print("Choice: ");

        MealType mealType;
        try {
            mealType = MealType.fromInput(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid meal type.");
            return;
        }

        List<Feedback> filtered = feedbackService.getByMealType(mealType);

        System.out.println("\n--- " + mealType + " Feedback (" + filtered.size() + " records) ---");
        for (Feedback fb : filtered) {
            System.out.println("  " + fb);
        }

        if (!filtered.isEmpty()) {
            double avg = analysisService.calculateAverage(filtered);
            System.out.printf("  Average rating: %.2f / 5%n", avg);
        }
    }

    static void generateReport() {
        List<Feedback> all    = feedbackService.getAllFeedback();
        List<Alert>    alerts = alertService.checkAllAlerts(all);
        String         report = analysisService.generateReport(all, alerts);
        System.out.println(report);
    }

    static void exportReport() {
        List<Feedback> all    = feedbackService.getAllFeedback();
        List<Alert>    alerts = alertService.checkAllAlerts(all);
        String         report = analysisService.generateReport(all, alerts);

        try {
            fileStorage.exportReport(report);
        } catch (IOException e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }

    static void viewAlerts() {
        List<Feedback> all    = feedbackService.getAllFeedback();
        List<Alert>    alerts = alertService.checkAllAlerts(all);

        if (alerts.isEmpty()) {
            System.out.println("\nNo active alerts. All meals within acceptable range.");
        } else {
            System.out.println("\n--- Active Alerts ---");
            for (Alert alert : alerts) {
                System.out.println("  " + alert);
            }
        }
    }

    static void weeklyComparison() {
        
        LocalDate today         = LocalDate.now();
        LocalDate thisWeekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate lastWeekStart = thisWeekStart.minusDays(7);
        LocalDate lastWeekEnd   = thisWeekStart.minusDays(1);

        List<Feedback> thisWeek = feedbackService.getByDateRange(thisWeekStart, today);
        List<Feedback> lastWeek = feedbackService.getByDateRange(lastWeekStart, lastWeekEnd);

        System.out.println("\n--- Week-over-Week Comparison ---");
        System.out.println(analysisService.weeklyComparison(thisWeek, lastWeek));


        Map<MealType, Double> thisWeekAvg = analysisService.averagePerMealType(thisWeek);
        Map<MealType, Double> lastWeekAvg = analysisService.averagePerMealType(lastWeek);

        System.out.println("\nPer meal breakdown:");
        for (MealType mt : MealType.values()) {
            double tw    = thisWeekAvg.containsKey(mt) ? thisWeekAvg.get(mt) : 0.0;
            double lw    = lastWeekAvg.containsKey(mt) ? lastWeekAvg.get(mt) : 0.0;
            String arrow = tw > lw ? "â†‘" : (tw < lw ? "â†“" : "â†’");
            System.out.printf("  %-12s  Last week: %.2f   This week: %.2f   %s%n",
                    mt, lw, tw, arrow);
        }
    }
}
