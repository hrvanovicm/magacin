package fyi.hrvanovicm.magacin.infrastructure.notification;

import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {
    private final ApplicationEventPublisher publisher;

    @Autowired
    public NotificationService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void notifyUser(String message, NotificationEventAction... actions) {
        publisher.publishEvent(new NotificationEvent(message, Duration.seconds(2), actions));
    }

    public void notifyUser(String message, Duration duration, NotificationEventAction... actions) {
        publisher.publishEvent(new NotificationEvent(message, duration, actions));
    }
}
