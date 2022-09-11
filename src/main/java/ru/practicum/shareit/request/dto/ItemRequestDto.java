package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
}
