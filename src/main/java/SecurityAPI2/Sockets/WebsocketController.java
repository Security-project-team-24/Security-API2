package SecurityAPI2.Sockets;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
@Controller
public class WebsocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @SendTo("/topic/notification")
    String sendNotification(String message) {
        return message;
    }
}
