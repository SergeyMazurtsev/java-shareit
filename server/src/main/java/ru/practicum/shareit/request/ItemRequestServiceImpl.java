package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.exception.IdViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final CommonService commonService;

    @Override
    public ItemRequestDtoOut createRequest(Long userId, LocalDateTime created, ItemRequestDtoIn requestDtoIn) {
        ItemRequest request = ItemRequestMapper.toRequest(requestDtoIn);
        request.setRequestor(commonService.getInDBUser(userId));
        request.setCreated(created);
        if (request.getDescription() == null) {
            throw new ValidatorException("Bad request. Description is null.");
        }
        if (request.getDescription().isEmpty()) {
            throw new ValidatorException("Bad request. Description is empty.");
        }
        try {
            return addItems(ItemRequestMapper.toRequestDtoOut(requestRepository.save(request)));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("Such request is already in base.");
        }
    }

    @Override
    public Set<ItemRequestDtoOut> getRequests(Long userId) {
        User user = commonService.getInDBUser(userId);
        Set<ItemRequestDtoOut> requestDtoOuts = requestRepository.findAllByRequestor(user).stream()
                .map(ItemRequestMapper::toRequestDtoOut).collect(Collectors.toSet());
        return requestDtoOuts.stream().map(this::addItems).collect(Collectors.toSet());
    }

    @Override
    public Set<ItemRequestDtoOut> getRequestsPage(Long userId, Integer from, Integer size) {
        User user = commonService.getInDBUser(userId);
        return requestRepository.findByRequestorNot(user, commonService.getPagination(from, size, "created"))
                .stream().map(ItemRequestMapper::toRequestDtoOut)
                .map(this::addItems).collect(Collectors.toSet());
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        User user = commonService.getInDBUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found in base."));
        return addItems(ItemRequestMapper.toRequestDtoOut(request));
    }

    private ItemRequestDtoOut addItems(ItemRequestDtoOut requestDtoOut) {
        requestDtoOut.setItems(commonService.getItemsByRequest(requestDtoOut.getId()));
        return requestDtoOut;
    }
}
