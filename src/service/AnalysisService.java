import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisService {

    // Common words to ignore during keyword analysis

    private static final List<String> STOP_WORDS = new ArrayList<>();

    static {
        STOP_WORDS.add("the"); STOP_WORDS.add("is");  STOP_WORDS.add("it");
        STOP_WORDS.add("was"); STOP_WORDS.add("and"); STOP_WORDS.add("a");
        STOP_WORDS.add("an");  STOP_WORDS.add("to");  STOP_WORDS.add("of");
        STOP_WORDS.add("in");  STOP_WORDS.add("i");   STOP_WORDS.add("very");
        STOP_WORDS.add("so");  STOP_WORDS.add("but"); STOP_WORDS.add("not");
        STOP_WORDS.add("for"); STOP_WORDS.add("on");  STOP_WORDS.add("are");
        STOP_WORDS.add("this");STOP_WORDS.add("with"); STOP_WORDS.add("my");
        STOP_WORDS.add("we");  STOP_WORDS.add("had"); STOP_WORDS.add("be");
        STOP_WORDS.add("at");  STOP_WORDS.add("too"); STOP_WORDS.add("food");
    }

    // Calculate average rating from a list of feedbacks
    public double calculateAverage(List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) return 0.0;
        int total = 0;
        for (Feedback fb : feedbacks) {
            total += fb.getRating();
        }
        return (double) total / feedbacks.size();
    }

    // Calculate average rating per meal type
    public Map<MealType, Double> averagePerMealType(List<Feedback> feedbacks) {
        Map<MealType, List<Integer>> ratingsByMeal = new HashMap<>();

        // Initialize all meal types with empty lists
        for (MealType mt : MealType.values()) {
            ratingsByMeal.put(mt, new ArrayList<>());
        }

        // Group ratings by meal type
        for (Feedback fb : feedbacks) {
            ratingsByMeal.get(fb.getMealType()).add(fb.getRating());
        }

        // Calculate average for each meal type
        Map<MealType, Double> averages = new HashMap<>();
        for (MealType mt : MealType.values()) {
            List<Integer> ratings = ratingsByMeal.get(mt);
            if (!ratings.isEmpty()) {
                int sum = 0;
                for (int r : ratings) sum += r;
                averages.put(mt, (double) sum / ratings.size());
            } else {
                averages.put(mt, 0.0);
            }
        }

        return averages;
    }

    // Find the meal type with the highest average rating
    public MealType getBestMeal(Map<MealType, Double> averages) {
        MealType best = null;
        double   max  = -1;
        for (MealType mt : MealType.values()) {
            double avg = averages.get(mt);
            if (avg > max) {
                max  = avg;
                best = mt;
            }
        }
        return best;
    }

    // Find the meal type with the lowest average rating
    public MealType getWorstMeal(Map<MealType, Double> averages) {
        MealType worst = null;
        double   min   = 6.0;
        for (MealType mt : MealType.values()) {
            double avg = averages.get(mt);
            if (avg > 0 && avg < min) {
                min   = avg;
                worst = mt;
            }
        }
        return worst;
    }

    // Count how many times each word appears across all comments
    public Map<String, Integer> getTopKeywords(List<Feedback> feedbacks, int topN) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (Feedback fb : feedbacks) {
            // Lowercase, remove punctuation, split into words
            String   cleaned = fb.getComment().toLowerCase().replaceAll("[^a-z ]", "");
            String[] words   = cleaned.split("\\s+");

            for (String word : words) {
                if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                    if (frequencyMap.containsKey(word)) {
                        frequencyMap.put(word, frequencyMap.get(word) + 1);
                    } else {
                        frequencyMap.put(word, 1);
                    }
                }
            }
        }

        // Convert map to list of entries so we can sort it
        List<String> keys = new ArrayList<>(frequencyMap.keySet());

        for (int i = 0; i < keys.size() - 1; i++) {
            for (int j = 0; j < keys.size() - i - 1; j++) {
                if (frequencyMap.get(keys.get(j)) < frequencyMap.get(keys.get(j + 1))) {
                    String temp = keys.get(j);
                    keys.set(j, keys.get(j + 1));
                    keys.set(j + 1, temp);
                }
            }
        }

        Map<String, Integer> topKeywords = new HashMap<>();
        for (int i = 0; i < topN && i < keys.size(); i++) {
            topKeywords.put(keys.get(i), frequencyMap.get(keys.get(i)));
        }

        return topKeywords;
    }

    // Get keywords from only low-rated feedback (rating 1 or 2)
    public Map<String, Integer> getComplaintKeywords(List<Feedback> feedbacks, int topN) {
        List<Feedback> poorFeedbacks = new ArrayList<>();
        for (Feedback fb : feedbacks) {
            if (fb.getRating() <= 2) {
                poorFeedbacks.add(fb);
            }
        }
        return getTopKeywords(poorFeedbacks, topN);
    }


    // WEEKLY COMPARISON

    public String weeklyComparison(List<Feedback> thisWeek, List<Feedback> lastWeek) {
        double thisAvg = calculateAverage(thisWeek);
        double lastAvg = calculateAverage(lastWeek);
        double diff    = thisAvg - lastAvg;

        String trend;
        if      (diff > 0) trend = "UP â†‘";
        else if (diff < 0) trend = "DOWN â†“";
        else               trend = "SAME â†’";

        return String.format(
            "This week: %.2f/5   |   Last week: %.2f/5   |   Trend: %s (%.2f)",
            thisAvg, lastAvg, trend, diff
        );
    }


    public String generateReport(List<Feedback> feedbacks, List<Alert> alerts) {
        if (feedbacks.isEmpty()) {
            return "No feedback data available to generate a report.";
        }

        Map<MealType, Double> averages   = averagePerMealType(feedbacks);
        MealType              bestMeal   = getBestMeal(averages);
        MealType              worstMeal  = getWorstMeal(averages);
        double                overallAvg = calculateAverage(feedbacks);
        Map<String, Integer>  keywords   = getTopKeywords(feedbacks, 10);
        Map<String, Integer>  complaints = getComplaintKeywords(feedbacks, 5);

        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("     MESS FEEDBACK ANALYSIS REPORT     \n");
        sb.append("========================================\n");
        sb.append(String.format("Total feedbacks  : %d%n",       feedbacks.size()));
        sb.append(String.format("Overall average  : %.2f / 5%n", overallAvg));
        sb.append(String.format("Best meal        : %s%n",       bestMeal));
        sb.append(String.format("Worst meal       : %s%n",       worstMeal));

        sb.append("\n--- Average rating per meal ---\n");
        for (MealType mt : MealType.values()) {
            double avg   = averages.get(mt);
            int    stars = (int) Math.round(avg);
            String bar   = "";
            for (int i = 0; i < stars; i++)     bar += "â˜…";
            for (int i = stars; i < 5; i++)     bar += "â˜†";
            sb.append(String.format("  %-12s  %s  %.2f/5%n", mt, bar, avg));
        }

        sb.append("\n--- Top keywords in all comments ---\n");
        for (String word : keywords.keySet()) {
            sb.append(String.format("  %-15s  %d times%n", word, keywords.get(word)));
        }

        if (!complaints.isEmpty()) {
            sb.append("\n--- Top complaint keywords (low-rated only) ---\n");
            for (String word : complaints.keySet()) {
                sb.append(String.format("  %-15s  %d times%n", word, complaints.get(word)));
            }
        }

        if (!alerts.isEmpty()) {
            sb.append("\n--- Active alerts ---\n");
            for (Alert a : alerts) {
                sb.append("  ").append(a).append("\n");
            }
        } else {
            sb.append("\n  No active alerts. All meals within acceptable range.\n");
        }

        sb.append("========================================\n");
        return sb.toString();
    }
}
