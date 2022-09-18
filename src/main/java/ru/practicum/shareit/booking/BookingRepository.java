package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerAndStatusLikeOrderByStartDesc(User user, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime start, Pageable pagination);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    @Query(value = "select * from bookings as b left join items as i on b.item_id = i.id " +
            "where i.owner_id = ?1 order by b.end_date desc", nativeQuery = true)
    List<Booking> findAllBookingOfUserInItem(Long userId, Pageable pageable);

    List<Booking> findAllByBookerAndEndBeforeAndStatusNot(User user, LocalDateTime now, BookingStatus rejected, Pageable pageable);
}
