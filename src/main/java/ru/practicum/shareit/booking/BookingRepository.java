package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    @Query(" select b " +
            "from Booking as b left join User as u on b.booker.id = u.id " +
            "where u.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndTimeBetweenOrderByStartDesc(long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime end);

    @Query(" select (count(b) > 0) " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "  and b.status = 'APPROVED' " +
            "  and b.start <= ?3 " +
            "  and b.end >= ?2")
    boolean isAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long itemOwnerId);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long itemOwnerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long itemOwnerId, LocalDateTime end);

    @Query(" select b " +
            "from Booking as b left join Item as i on b.item.id = i.id " +
            "left join User as u on i.owner.id = u.id " +
            "where u.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByItemOwnerIdAndTimeBetweenOrderByStartDesc(long itemOwnerId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long itemOwnerId, Status status);
}
