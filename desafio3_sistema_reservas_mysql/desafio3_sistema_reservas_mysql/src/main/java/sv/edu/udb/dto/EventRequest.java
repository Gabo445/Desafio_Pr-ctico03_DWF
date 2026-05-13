package sv.edu.udb.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDateTime eventDate;
    @NotBlank
    private String venue;
    @NotNull @Min(1)
    private Integer capacity;
    @NotNull @DecimalMin("0.00")
    private BigDecimal pricePerTicket;
}
