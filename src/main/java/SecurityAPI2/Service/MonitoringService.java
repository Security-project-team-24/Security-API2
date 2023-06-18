package SecurityAPI2.Service;

import SecurityAPI2.Service.Email.IEmailService;
import SecurityAPI2.utils.LogModel;
import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final IEmailService emailService; 
    private final SimpMessagingTemplate simpMessagingTemplate;


    void analyseThreats() {
        List<LogModel> lines = readLogFile();
        LocalDateTime minutesAgo = LocalDateTime.now().minusMinutes(10);
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        List<LogModel> filtered = extractForAPeriod(lines, minutesAgo, now);
        HashMap<String, Integer> loginAttempts = mapLoginAttempts(filtered);
        warnIfLoginAttemptNotNormal(loginAttempts);
        warnIfPermissionsUpdated(filtered);
    }


    HashMap<String, Integer> mapLoginAttempts(List<LogModel> logs) {
        HashMap<String, Integer> loginAttempts = new HashMap<>();

        logs.forEach(log -> {
            if(!log.isLoginAttempt()) return;
            String ipAddress = log.getIpAddress();
            if(!loginAttempts.containsKey(ipAddress)) {
                loginAttempts.put(ipAddress, 0);
            }
            loginAttempts.put(ipAddress, loginAttempts.get(ipAddress) + 1);
        });
        return loginAttempts;
    }

    void warnIfPermissionsUpdated(List<LogModel> logs) {
        logs.forEach(log -> {
            if(!log.isPermissionCommitted()) return;
            String message = log.getEmailFromPermissionCommittedMessage() + " updated permissions!";
            String subject = "Permissions updated";
            emailService.sendWarningEmail(message,subject);
            simpMessagingTemplate.convertAndSend("/topic/notification", message);
        });
    }

    void warnIfLoginAttemptNotNormal(HashMap<String, Integer> loginAttempts) {
        Set<String> keys = loginAttempts.keySet();
        keys.forEach(key -> {
            int value = loginAttempts.get(key);
            if (value > 10) {
                String message = "Ip address: " + key + "has attempted login " + value + " times in last 10 minutes";
                String subject = "Login attempts warning!";
                emailService.sendWarningEmail(message, subject);
                simpMessagingTemplate.convertAndSend("/topic/notification", message);
            }
        });

    }

    List<LogModel> extractForAPeriod(List<LogModel> logs, LocalDateTime from, LocalDateTime to) {
        return logs.stream()
                .filter(log->log.isInPeriod(from,to))
                .toList();
    }

    List<LogModel> readLogFile() {
        BufferedReader reader;
        List<LogModel> lines = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader("./logs/spring-boot-logger.log"));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line == null) continue;
                LogModel log = LogModel.parse(line);
                if(log != null)
                    lines.add(log);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;

    }
}
