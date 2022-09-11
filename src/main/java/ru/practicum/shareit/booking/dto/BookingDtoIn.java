package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoIn {
    private LocalDateTime start;
    private LocalDateTime end;
    @JsonProperty("itemId")
    private Long item;
}
