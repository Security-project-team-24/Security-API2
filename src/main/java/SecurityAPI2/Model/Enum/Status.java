package SecurityAPI2.Model.Enum;

public enum Status {
    PENDING(0),
    APPROVED(1),
    DISAPPROVED(2),
    ACTIVATED(3);

    private final int value;
    Status(int number) {
        this.value = number;
    }

    public int getValue() {
        return this.value;
    }
}
