import java.time.LocalDate;

public class Alert {

    public enum Severity {
        WARNING,   // average rating below 3.0
        CRITICAL   // average rating below 2.0
    }

    private MealType  mealType;
    private double    averageRating;
    private Severity  severity;
    private String    message;
    private LocalDate date;

    public Alert(MealType mealType, double averageRating, Severity severity, LocalDate date) {
        this.mealType      = mealType;
        this.averageRating = averageRating;
        this.severity      = severity;
        this.date          = date;
        this.message       = buildMessage();
    }

    private String buildMessage() {
        String level = (severity == Severity.CRITICAL) ? "!! CRITICAL !!" : "! WARNING !";
        return level + "  " + mealType
             + " rating dropped to "
             + String.format("%.2f", averageRating) + "/5"
             + "  on " + date;
    }

    public MealType  getMealType()      { return mealType;      }
    public double    getAverageRating() { return averageRating; }
    public Severity  getSeverity()      { return severity;      }
    public String    getMessage()       { return message;       }
    public LocalDate getDate()          { return date;          }

    @Override
    public String toString() {
        return message;
    }
}
