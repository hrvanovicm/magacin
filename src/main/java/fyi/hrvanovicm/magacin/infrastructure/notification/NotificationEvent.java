package fyi.hrvanovicm.magacin.infrastructure.notification;

import javafx.application.Platform;
import javafx.util.Duration;
import lombok.Getter;
import org.springframework.context.event.EventListener;

import java.util.List;

public class NotificationEvent {
    @Getter
    final String message;

    @Getter
    final Duration duration;

    @Getter
    final List<NotificationEventAction> actions;

    public NotificationEvent(String message, Duration duration, NotificationEventAction... actions) {
        this.message = message;
        this.duration = duration;
        this.actions = List.of(actions);
    }
}
