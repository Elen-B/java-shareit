package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerId(Long bookerId, Sort sort);

    Collection<Booking> findByBookerIdAndEndDateBefore(Long bookerId, LocalDateTime endDate, Sort sort);

    Collection<Booking> findByBookerIdAndStartDateAfter(Long bookerId, LocalDateTime startDate, Sort sort);

    Collection<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(Long bookerId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

    Collection<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    Collection<Booking> findByItemIdAndBookerIdAndEndDateBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime endDate, BookingStatus status);

    Collection<Booking> findBookingByItem_OwnerId(Long ownerId, Sort sort);

    Collection<Booking> findBookingByEndDateBeforeAndItem_OwnerId(LocalDateTime endDate, Long ownerId, Sort sort);

    Collection<Booking> findBookingByStartDateAfterAndItem_OwnerId(LocalDateTime startDate, Long ownerId, Sort sort);

    Collection<Booking> findBookingByStartDateBeforeAndEndDateAfterAndItem_OwnerId(LocalDateTime startDate, LocalDateTime endDate, Long ownerId, Sort sort);

    Collection<Booking> findBookingByStatusAndItem_OwnerId(BookingStatus status, Long ownerId, Sort sort);

    @Query(" select new ru.practicum.shareit.booking.model.BookingDates(i.id, MAX(b.startDate), MAX(b.endDate)) " +
            " from Booking b " +
            " join b.item i " +
            " join i.owner o " +
            "where b.startDate < ?1 " +
            "  and o.id = ?2 " +
            "group by i.id")
    Collection<BookingDates> lastBookings(LocalDateTime date, Long ownerId);

    @Query(" select new ru.practicum.shareit.booking.model.BookingDates(i.id, MIN(b.startDate), MIN(b.endDate)) " +
            " from Booking b " +
            " join b.item i " +
            " join i.owner o " +
            "where b.startDate >= ?1 " +
            "  and o.id = ?2 " +
            "group by i.id")
    Collection<BookingDates> nextBookings(LocalDateTime date, Long ownerId);
}
