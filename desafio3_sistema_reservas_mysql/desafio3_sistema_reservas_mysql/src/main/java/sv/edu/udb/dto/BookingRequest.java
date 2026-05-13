package sv.edu.udb.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull
    private Integer eventId;
    @NotNull @Min(1)
    private Integer quantity;
}
