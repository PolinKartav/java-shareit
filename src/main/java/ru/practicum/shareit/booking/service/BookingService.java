package ru.practicum.shareit.booking.service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, CreateUpdateBookingDto createUpdateBookingDto);

    BookingDto confirmedBooking(Long userId, Long bookingId, boolean approved);
    BookingDto getById(Long userId, Long bookingId);
    List<BookingDto> getBookingsOfBooker(State state, Long bookerId);
    List<BookingDto> getBookingsOfOwner(State state, Long userId);

}
