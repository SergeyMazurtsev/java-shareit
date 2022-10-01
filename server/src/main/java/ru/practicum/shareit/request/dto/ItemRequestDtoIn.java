package ru.practicum.shareit.request.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoIn {
    private Long id;
    private String description;
}
