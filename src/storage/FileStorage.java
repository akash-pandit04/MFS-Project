import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String FEEDBACK_FILE = "data/feedback.csv";
    private static final String REPORT_FILE   = "data/weekly_report.txt";

    // Save all feedbacks to CSV 
    public void saveAll(List<Feedback> feedbacks) throws IOException {
        ensureDataFolderExists();

        BufferedWriter writer = new BufferedWriter(new FileWriter(FEEDBACK_FILE, false));
        try {

            writer.write("studentId,mealType,rating,comment,date");
            writer.newLine();

            for (Feedback fb : feedbacks) {
                writer.write(fb.toCsv());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public void appendFeedback(Feedback feedback) throws IOException {
        ensureDataFolderExists();

        File file      = new File(FEEDBACK_FILE);
        boolean isNew  = !file.exists() || file.length() == 0;

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        try {

            if (isNew) {
                writer.write("studentId,mealType,rating,comment,date");
                writer.newLine();
            }
            writer.write(feedback.toCsv());
            writer.newLine();
        } finally {
            writer.close();
        }
    }

    // Load all feedbacks from CSV file
    public List<Feedback> loadAll() throws IOException {
        List<Feedback> feedbacks = new ArrayList<>();
        File file = new File(FEEDBACK_FILE);

        // If file doesn't exist return empty list
        if (!file.exists() || file.length() == 0) {
            return feedbacks;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String  line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        feedbacks.add(Feedback.fromCsv(line));
                    } catch (Exception e) {
                        System.out.println("Skipping bad line: " + line);
                    }
                }
            }
        } finally {
            reader.close();
        }

        return feedbacks;
    }

    // Export the analysis report string to a .txt file
    public void exportReport(String reportContent) throws IOException {
        ensureDataFolderExists();

        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE, false));
        try {
            writer.write("Mess Feedback System â€” Weekly Report");
            writer.newLine();
            writer.write("Generated on: " + LocalDate.now());
            writer.newLine();
            writer.write("=".repeat(40));
            writer.newLine();
            writer.newLine();
            writer.write(reportContent);
        } finally {
            writer.close();
        }

        System.out.println("Report saved to: " + REPORT_FILE);
    }

    private void ensureDataFolderExists() {
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
}
