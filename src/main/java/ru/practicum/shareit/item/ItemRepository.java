package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(User user);
    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(String toLowerCase, String toLowerCase1, Pageable pageable);
    List<Item> findAllByRequest_Id(Long requestId);
    List<Item> findAllByOwner(User user, Pageable pageable);
}
