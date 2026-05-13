package sv.edu.udb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sv.edu.udb.model.Event;
import sv.edu.udb.model.Role;
import sv.edu.udb.model.User;
import sv.edu.udb.repository.EventRepository;
import sv.edu.udb.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("gabriel")) {
            userRepository.save(User.builder()
                    .username("gabriel")
                    .password(passwordEncoder.encode("123456"))
                    .firstname("Gabriel")
                    .lastname("Echegoyen")
                    .age(20)
                    .role(Role.ROLE_USER)
                    .build());
        }
        if (eventRepository.count() == 0) {
            eventRepository.save(Event.builder()
                    .title("Conferencia de Tecnología")
                    .description("Evento sobre desarrollo web, APIs REST y seguridad JWT.")
                    .eventDate(LocalDateTime.now().plusDays(10).withHour(9).withMinute(0))
                    .venue("Auditorio Universidad Don Bosco")
                    .capacity(100)
                    .pricePerTicket(new BigDecimal("5.00"))
                    .build());
            eventRepository.save(Event.builder()
                    .title("Taller de Spring Boot")
                    .description("Práctica guiada para crear servicios backend con Spring Boot.")
                    .eventDate(LocalDateTime.now().plusDays(15).withHour(14).withMinute(0))
                    .venue("Laboratorio de Cómputo")
                    .capacity(40)
                    .pricePerTicket(new BigDecimal("3.50"))
                    .build());
        }
    }
}
