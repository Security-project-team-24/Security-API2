package SecurityAPI2.Model.Enum;

public enum UserRole {
    ENGINEER("ENGINEER"),
    PROJECT_MANAGER("PROJECT_MANAGER"),
    HR_MANAGER("HRMANAGER"),
    ADMIN("ADMIN");
    private final String value;
    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
