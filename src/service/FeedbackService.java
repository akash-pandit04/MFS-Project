import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedbackService {

    private List<Feedback> feedbackList;


    public FeedbackService() {
        this.feedbackList = new ArrayList<>();
    }

    // Start with data already loaded from file
    public FeedbackService(List<Feedback> existingData) {
        this.feedbackList = new ArrayList<>(existingData);
    }

    // Add a new feedback entry after validating input
    public void addFeedback(String studentId, MealType mealType, int rating, String comment) {
        
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }

        Feedback fb = new Feedback(studentId, mealType, rating, comment, LocalDate.now());
        feedbackList.add(fb);
    }

    // Get all feedback sorted by date
    public List<Feedback> getAllFeedback() {
        List<Feedback> sorted = new ArrayList<>(feedbackList);
        Collections.sort(sorted);
        return sorted;
    }

    // Get feedback for one specific meal type
    public List<Feedback> getByMealType(MealType mealType) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            if (fb.getMealType() == mealType) {
                result.add(fb);
            }
        }
        return result;
    }

    // Get feedback submitted today
    public List<Feedback> getTodaysFeedback() {
        List<Feedback> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Feedback fb : feedbackList) {
            if (fb.getDate().equals(today)) {
                result.add(fb);
            }
        }
        return result;
    }

    // Get feedback within a date range 
    public List<Feedback> getByDateRange(LocalDate from, LocalDate to) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            if (!fb.getDate().isBefore(from) && !fb.getDate().isAfter(to)) {
                result.add(fb);
            }
        }
        return result;
    }

    // Get feedback submitted by a specific student
    public List<Feedback> getByStudent(String studentId) {
        List<Feedback> result = new ArrayList<>();
        for (Feedback fb : feedbackList) {
            if (fb.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(fb);
            }
        }
        return result;
    }

    public int getTotalCount() {
        return feedbackList.size();
    }
}
