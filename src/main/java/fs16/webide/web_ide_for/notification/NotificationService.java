package fs16.webide.web_ide_for.notification;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, String> notification = new ConcurrentHashMap<>();

    public void push(Long userId, String message){
        notification.put(userId,message);
    }

    public Optional<String> pop(Long userId){
        return Optional.ofNullable(notification.remove(userId));
    }
}
