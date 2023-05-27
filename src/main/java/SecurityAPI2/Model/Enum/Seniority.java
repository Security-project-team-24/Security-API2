package SecurityAPI2.Model.Enum;

public enum Seniority {
    JUNIOR("JUNIOR"),
    MEDIOR("MEDIOR"),
    SENIOR("SENIOR");

    private final String value;

    Seniority(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
