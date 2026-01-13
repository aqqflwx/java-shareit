package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                                LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findByOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findByOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3")
    List<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                               LocalDateTime end, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemId(Long itemId, Sort sort);

    @Query("select b from Booking b where b.item.id = ?1 and b.start < ?2 and b.status = ?3")
    List<Booking> findByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime now,
                                                        BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.id = ?1 and b.start > ?2 and b.status = ?3")
    List<Booking> findByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now,
                                                       BookingStatus status, Sort sort);
}
