package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BookingDates {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDates bookingDates)) return false;
        return (Objects.equals(itemId, bookingDates.getItemId()) &&
                Objects.equals(start, bookingDates.getStart()) &&
                Objects.equals(end, bookingDates.getEnd()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
