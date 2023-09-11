package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.ValuesAllowedConstraint;

import java.util.List;

import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                    @RequestBody CreateUpdateBookingDto createUpdateBookingDto) {
        return bookingService.createBooking(userId, createUpdateBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmedBooking(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam boolean approved) {
        return bookingService.confirmedBooking(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                              @ValuesAllowedConstraint(propName = "state",
                                                      values = {"all",
                                                              "current",
                                                              "past",
                                                              "future",
                                                              "waiting",
                                                              "rejected"},
                                                      message = "Unknown state: UNSUPPORTED_STATUS")
                                              @RequestParam(defaultValue = "all") String state,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size) {
        return bookingService.getBookingsOfBooker(State.valueOf(state.toUpperCase()), userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                               @ValuesAllowedConstraint(propName = "state",
                                                       values = {"all",
                                                               "current",
                                                               "past",
                                                               "future",
                                                               "waiting",
                                                               "rejected"},
                                                       message = "Unknown state: UNSUPPORTED_STATUS")
                                               @RequestParam(defaultValue = "all") String state,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        return bookingService.getBookingsOfOwner(State.valueOf(state.toUpperCase()), userId, from, size);
    }

}
