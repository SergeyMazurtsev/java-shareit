package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CommonGetItemAndUser {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Item getInDbItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Not found."));
    }

    public User getInDBUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found."));
    }
}
