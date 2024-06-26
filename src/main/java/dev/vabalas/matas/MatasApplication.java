package dev.vabalas.matas;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Push
@PWA(name = "Matas Progressive Web Application", shortName = "MatasPWA")
public class MatasApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MatasApplication.class, args);
    }
}
