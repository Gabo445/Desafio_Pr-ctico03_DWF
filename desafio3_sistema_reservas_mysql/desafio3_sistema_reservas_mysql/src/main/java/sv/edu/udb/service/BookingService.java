package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.dto.BookingRequest;
import sv.edu.udb.exception.BadRequestException;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.*;
import sv.edu.udb.repository.BookingRepository;
import sv.edu.udb.repository.EventRepository;
import sv.edu.udb.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(BookingRequest request, String username) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new BadRequestException("La cantidad de entradas debe ser mayor o igual a 1");
        }

        Integer reserved = bookingRepository.sumConfirmedQuantityByEventId(event.getIdEvent(), BookingStatus.CONFIRMED);
        if (reserved + request.getQuantity() > event.getCapacity()) {
            throw new BadRequestException("No hay cupos suficientes para este evento. Cupos disponibles: " + (event.getCapacity() - reserved));
        }

        Booking booking = Booking.builder()
                .event(event)
                .user(user)
                .quantity(request.getQuantity())
                .totalAmount(event.getPricePerTicket().multiply(java.math.BigDecimal.valueOf(request.getQuantity())))
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();
        return bookingRepository.save(booking);
    }

    public List<Booking> myBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return bookingRepository.findByUser(user);
    }

    @Transactional
    public Booking cancelBooking(Integer id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        if (!booking.getUser().getUsername().equals(username)) {
            throw new BadRequestException("No puedes cancelar una reserva de otro usuario");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("La reserva ya está cancelada");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
