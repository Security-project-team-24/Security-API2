package SecurityAPI2.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoringService {




    @Scheduled(fixedDelay = 5000)
    void analyseThreats() {
        List<String> lines = readLogFile();
        LocalDateTime minutesAgo = LocalDateTime.now().minusMinutes(10);
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);

        List<String> filtered = extractForAPeriod(lines, minutesAgo, now);
    }

    List<String> extractForAPeriod(List<String> logs, LocalDateTime from, LocalDateTime to) {
        return logs.stream().filter(log -> {
            if(log == null) return false;
            String[] chunks = log.split(" ");
            String date = chunks[0];
            String time = chunks[1];
            String timeFiltered = time.split(",")[0];
            String result = date + " " +timeFiltered;
            String pattern = "yyyy-MM-dd HH:mm:ss";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            try {
                LocalDateTime dt = LocalDateTime.parse(result, formatter);
                return dt.isAfter(from) && dt.isBefore(to);
            } catch(Exception e) {
                return false;
            }


        }).toList();
    }

    List<String> readLogFile() {
        BufferedReader reader;
        List<String> lines = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader("./logs/spring-boot-logger.log"));
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                line = reader.readLine();

                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }
}
