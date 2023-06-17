package SecurityAPI2.utils;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


enum LogLevel {
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    private String val;

    LogLevel(String val) {
        this.val = val;
    }

    String getValue() {
        return this.val;
    }

    static LogLevel FromVal(String val) {
        if(val.equals("WARN")) return LogLevel.WARN;
        if(val.equals("ERROR")) return LogLevel.ERROR;
        return LogLevel.INFO;
    }
}
@Getter
@Setter
public class LogModel {

    private LocalDateTime time;
    private LogLevel level;
    private String loggedInClass;
    private String message;


    protected LogModel(LocalDateTime time, String level, String loggedInClass, String message) {
        this.time = time;
        this.level = LogLevel.FromVal(level);
        this.loggedInClass = loggedInClass;
        this.message = message;
    }

    public static LogModel parse(String text) {
        if(text == null || !isApplicationError(text)) {
            return null;
        }
        String content = text.substring(14);
        String[] chunks = content.split(" ");
        String dateChunks = chunks[0] + "T" + chunks[1];
        LocalDateTime dateTime = parseDateTime(dateChunks);
        String[] messageChunks = Arrays.copyOfRange(chunks, 4, chunks.length);
        String message = String.join(" ", messageChunks);

        return new LogModel(dateTime, chunks[2], chunks[4], message);
    }

    private static boolean isApplicationError(String text) {
        Pattern pattern = Pattern.compile("\\[APPLICATION\\]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public boolean isInPeriod(LocalDateTime from, LocalDateTime to) {
        return time.isAfter(from) && time.isBefore(to);
    }

    public String getIpAddress() {
        String[] chunks = message.split(",");
        String text = chunks[chunks.length - 1];
        return text.split("Ip:")[1].trim();
    }

    public String getEmailFromPermissionCommittedMessage() {
        String[] chunks = this.message.split(" ");
        return chunks[1].split(":")[1];
    }

    public boolean isLoginAttempt() {
        Pattern pattern = Pattern.compile("Route: /auth/login, Type: REQUEST", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("Route: /login, Type: REQUEST", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.message);
        Matcher matcher2 = pattern2.matcher(this.message);
        return matcher.find() | matcher2.find();
    }

    public boolean isPermissionCommitted() {
        Pattern pattern = Pattern.compile("PERMISSIONS_COMMITTED", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.message);
        return matcher.find();
    }

    private static LocalDateTime parseDateTime(String dt) {
        try {
            return LocalDateTime.parse(dt.split(",")[0]);
        } catch(Exception e) {
            e.printStackTrace();
            return LocalDateTime.MIN;
        }
    }
}
