package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b join fetch b.item as i join fetch i.owner as o " +
            " where o.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b join fetch b.item as i join fetch i.owner as o " +
            " where o.id = :ownerId and b.status = :status")
    List<Booking> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status, Pageable pageable);

    @Query(value = "select b from Booking b join fetch b.item as i join fetch i.owner as o " +
            " where o.id = :ownerId and b.start > :dateTime")
    List<Booking> findAllByOwnerIdAndStartAfter(@Param("ownerId") Long ownerId,
                                                @Param("dateTime") LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b join fetch b.item as i join fetch i.owner as o " +
            " where o.id = :ownerId and b.end < :dateTime")
    List<Booking> findAllByOwnerIdAndEndBefore(@Param("ownerId") Long ownerId,
                                               @Param("dateTime") LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b join fetch b.item as i join fetch i.owner as o " +
            " where o.id = :ownerId and b.start < :dateTime and b.end > :dateTime")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                            @Param("dateTime") LocalDateTime dateTime,
                                                            Pageable pageable);

}
