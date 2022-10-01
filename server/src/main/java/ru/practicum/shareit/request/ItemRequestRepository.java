package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Set;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Set<ItemRequest> findAllByRequestor(User user);

    List<ItemRequest> findByRequestorNot(User user, Pageable pageable);
}
