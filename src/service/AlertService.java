import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertService {

    private static final double WARNING_THRESHOLD  = 3.0;
    private static final double CRITICAL_THRESHOLD = 2.0;

    // Check alerts across all meal types 
    public List<Alert> checkAllAlerts(List<Feedback> feedbacks) {
        List<Alert> alerts = new ArrayList<>();

        for (MealType mealType : MealType.values()) {

            // Filter feedback for this meal type only
            List<Feedback> mealFeedbacks = new ArrayList<>();
            for (Feedback fb : feedbacks) {
                if (fb.getMealType() == mealType) {
                    mealFeedbacks.add(fb);
                }
            }

            if (mealFeedbacks.isEmpty()) continue;

            // Group ratings by date 
            Map<LocalDate, List<Integer>> ratingsByDate = new HashMap<>();
            for (Feedback fb : mealFeedbacks) {
                if (!ratingsByDate.containsKey(fb.getDate())) {
                    ratingsByDate.put(fb.getDate(), new ArrayList<>());
                }
                ratingsByDate.get(fb.getDate()).add(fb.getRating());
            }

            // Collect all unique dates and sort them 
            List<LocalDate> sortedDates = new ArrayList<>(ratingsByDate.keySet());
            for (int i = 0; i < sortedDates.size() - 1; i++) {
                for (int j = 0; j < sortedDates.size() - i - 1; j++) {
                    if (sortedDates.get(j).isAfter(sortedDates.get(j + 1))) {
                        LocalDate temp = sortedDates.get(j);
                        sortedDates.set(j, sortedDates.get(j + 1));
                        sortedDates.set(j + 1, temp);
                    }
                }
            }

            // Check if average dropped below threshold for 3 or more days
            int warningStreak  = 0;
            int criticalStreak = 0;

            for (LocalDate date : sortedDates) {
                List<Integer> ratings = ratingsByDate.get(date);

                // Calculate average for this date 
                int sum = 0;
                for (int r : ratings) sum += r;
                double avg = (double) sum / ratings.size();

                if (avg <= CRITICAL_THRESHOLD) {
                    criticalStreak++;
                    warningStreak = 0;
                    if (criticalStreak >= 3) {
                        alerts.add(new Alert(mealType, avg, Alert.Severity.CRITICAL, date));
                    }
                } else if (avg <= WARNING_THRESHOLD) {
                    warningStreak++;
                    criticalStreak = 0;
                    if (warningStreak >= 3) {
                        alerts.add(new Alert(mealType, avg, Alert.Severity.WARNING, date));
                    }
                } else {
                
                    warningStreak  = 0;
                    criticalStreak = 0;
                }
            }
        }

        return alerts;
    }

    public Alert checkImmediateAlert(List<Feedback> todaysFeedbacks, MealType mealType) {
        List<Feedback> mealToday = new ArrayList<>();
        for (Feedback fb : todaysFeedbacks) {
            if (fb.getMealType() == mealType) {
                mealToday.add(fb);
            }
        }

        if (mealToday.isEmpty()) return null;

        int sum = 0;
        for (Feedback fb : mealToday) sum += fb.getRating();
        double avg = (double) sum / mealToday.size();

        if (avg <= CRITICAL_THRESHOLD) {
            return new Alert(mealType, avg, Alert.Severity.CRITICAL, LocalDate.now());
        } else if (avg <= WARNING_THRESHOLD) {
            return new Alert(mealType, avg, Alert.Severity.WARNING, LocalDate.now());
        }

        return null;
    }
}
