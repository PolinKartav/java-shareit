package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.validator.ValuesAllowedConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class GatewayBookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                    @Valid @RequestBody CreateUpdateBookingDto createUpdateBookingDto) {
        return client.create(userId, createUpdateBookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId, @PathVariable Long bookingId) {
        return client.getBookingByUserOwner(userId, bookingId);
    }
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object>  confirmedBooking(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam boolean approved) {
        return client.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                              @ValuesAllowedConstraint(propName = "state",
                                                      values = {"all",
                                                              "current",
                                                              "past",
                                                              "future",
                                                              "waiting",
                                                              "rejected"},
                                                      message = "Unknown state: UNSUPPORTED_STATUS")
                                              @RequestParam(defaultValue = "all") String state,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "20") @Positive int size) {
        return client.getUserBookings(userId, String.valueOf(State.valueOf(state.toUpperCase())), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                               @ValuesAllowedConstraint(propName = "state",
                                                       values = {"all",
                                                               "current",
                                                               "past",
                                                               "future",
                                                               "waiting",
                                                               "rejected"},
                                                       message = "Unknown state: UNSUPPORTED_STATUS")
                                               @RequestParam(defaultValue = "all") String state,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "20") @Positive int size) {
        return client.getOwnerBookings(userId, String.valueOf(State.valueOf(state.toUpperCase())), from, size);
    }

}
