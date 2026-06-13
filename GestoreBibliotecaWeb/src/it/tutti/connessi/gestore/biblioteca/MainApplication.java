package it.tutti.connessi.gestore.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        // Avvia il server Web integrato (Tomcat di default sulla porta 8080)
        SpringApplication.run(MainApplication.class, args);
        System.out.println("🚀 Backend della Biblioteca avviato correttamente su http://localhost:8080");
    }
}