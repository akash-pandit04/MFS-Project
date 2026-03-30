import java.time.LocalDate;

public class Feedback implements Comparable<Feedback> {

    private String    studentId;
    private MealType  mealType;
    private int       rating;      // 1 to 5
    private String    comment;
    private LocalDate date;

 
    public Feedback(String studentId, MealType mealType, int rating, String comment, LocalDate date) {
        this.studentId = studentId;
        this.mealType  = mealType;
        this.rating    = rating;
        this.comment   = comment;
        this.date      = date;
    }

  
    public String    getStudentId(){
         return studentId;
         }
    public MealType  getMealType() {
         return mealType;
          }
    public int       getRating() {
         return rating;    
        }
    public String    getComment()   {
         return comment;  
         }
    public LocalDate getDate()      {
         return date;     
         }

    // Sort feedback by date 
    @Override
    public int compareTo(Feedback other) {
        return this.date.compareTo(other.date);
    }

    public String toCsv() {
        return studentId + ","
             + mealType.name() + ","
             + rating + ","
             + comment.replace(",", ";") + ","
             + date.toString();
    }
    public static Feedback fromCsv(String line) {
        String[] parts = line.split(",", 5);
        String    studentId = parts[0].trim();
        MealType  mealType  = MealType.valueOf(parts[1].trim());
        int       rating    = Integer.parseInt(parts[2].trim());
        String    comment   = parts[3].trim();
        LocalDate date      = LocalDate.parse(parts[4].trim());
        return new Feedback(studentId, mealType, rating, comment, date);
    }

    @Override
    public String toString() {
        return "[" + date + "]  " + mealType
             + "  |  Rating: " + rating + "/5"
             + "  |  " + studentId
             + "  |  \"" + comment + "\"";
    }
}
