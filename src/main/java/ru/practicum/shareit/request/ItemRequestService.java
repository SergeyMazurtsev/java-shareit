package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public interface ItemRequestService {
    ItemRequestDtoOut createRequest(Long userId, LocalDateTime created, ItemRequestDtoIn requestDtoIn);

    Set<ItemRequestDtoOut> getRequests(Long userId);

    Set<ItemRequestDtoOut> getRequestsPage(Long userId, Integer from, Integer size);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}
