package fyi.hrvanovicm.magacin;

import fyi.hrvanovicm.magacin.presentation.javafx.app.JavaFxApplication;
import javafx.application.Application;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DesktopApplication {
    public static void main(String[] args) {
        // It's triggered because Jasper doesn't work.
        System.setProperty("java.awt.headless", "false");

        Application.launch(JavaFxApplication.class, args);
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }
}
