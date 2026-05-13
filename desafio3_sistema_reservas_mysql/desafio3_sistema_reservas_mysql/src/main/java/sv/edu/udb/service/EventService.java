package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.EventRequest;
import sv.edu.udb.exception.BadRequestException;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Event;
import sv.edu.udb.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
    }

    public Event create(EventRequest request) {
        validateEvent(request);
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .venue(request.getVenue())
                .capacity(request.getCapacity())
                .pricePerTicket(request.getPricePerTicket())
                .build();
        return eventRepository.save(event);
    }

    public Event update(Integer id, EventRequest request) {
        validateEvent(request);
        Event event = findById(id);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue());
        event.setCapacity(request.getCapacity());
        event.setPricePerTicket(request.getPricePerTicket());
        return eventRepository.save(event);
    }

    public void delete(Integer id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }

    private void validateEvent(EventRequest request) {
        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a 0");
        }
        if (request.getPricePerTicket() == null || request.getPricePerTicket().signum() < 0) {
            throw new BadRequestException("El precio no puede ser negativo");
        }
    }
}
