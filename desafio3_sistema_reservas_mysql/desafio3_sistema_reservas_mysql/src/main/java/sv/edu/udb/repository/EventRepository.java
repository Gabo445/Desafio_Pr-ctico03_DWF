package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.model.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
