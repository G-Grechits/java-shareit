package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end,
                                                             Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime end);

    @Query(" select (count(b) > 0) " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "  and b.status = 'APPROVED' " +
            "  and b.start <= ?3 " +
            "  and b.end >= ?2")
    boolean isNotAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId, long itemId, Status status,
                                                                  LocalDateTime end);

    List<Booking> findAllByItemOwnerId(long itemOwnerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(long itemOwnerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(long itemOwnerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(long itemOwnerId, LocalDateTime start, LocalDateTime end,
                                                                Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(long itemOwnerId, Status status, Pageable pageable);
}
