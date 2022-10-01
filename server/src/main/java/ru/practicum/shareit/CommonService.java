package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Item getInDbItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found in base."));
    }

    public User getInDBUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found in base."));
    }

    public Set<ItemDto> getItemsByRequest(Long requestId) {
        return itemRepository.findAllByRequest_Id(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toSet());
    }

    public Pageable getPagination(Integer from, Integer size, String sortField) {
        if ((from < 0 || size < 0) || (size == 0)) {
            throw new ValidatorException("Bad request with pagination parameters.");
        }
        if (sortField != null) {
            return PageRequest.of(from / size, size, Sort.by(sortField).descending());
        } else {
            return PageRequest.of(from / size, size);
        }
    }
}
