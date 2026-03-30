public enum MealType {
    BREAKFAST,
    LUNCH,
    SNACKS,
    DINNER;

    public static MealType fromInput(String input) {
        switch (input.trim().toUpperCase()) {
            case "1": case "BREAKFAST": return BREAKFAST;
            case "2": case "LUNCH":     return LUNCH;
            case "3": case "SNACKS":    return SNACKS;
            case "4": case "DINNER":    return DINNER;
            default: throw new IllegalArgumentException("Invalid choice: " + input);
        }
    }
}
