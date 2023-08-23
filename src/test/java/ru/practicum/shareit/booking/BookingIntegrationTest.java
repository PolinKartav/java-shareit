package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private static CreateUpdateUserDto userDto;
    private static CreateUpdateUserDto userDto2;
    private static CreateUpdateItemDto itemDto;
    private static CreateUpdateBookingDto bookingDto;

    @BeforeAll
    static void beforeAll() {
        userDto = CreateUpdateUserDto.builder()
                .name("userName")
                .email("email@ya.ru")
                .build();

        userDto2 = CreateUpdateUserDto.builder()
                .name("userName2")
                .email("email2@ya.ru")
                .build();

        itemDto = CreateUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        bookingDto = CreateUpdateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void shouldCreateBooking() {
        userService.createUser(userDto);
        userService.createUser(userDto2);
        itemService.createItem(1L, itemDto);
        bookingService.createBooking(2L, bookingDto);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(booking.getItem().getId()));
        assertThat(2L, equalTo(booking.getBooker().getId()));
        assertThat(Status.WAITING, equalTo(booking.getStatus()));
    }

    @Test
    void shouldApproveBooking() {
        userService.createUser(userDto);
        userService.createUser(userDto2);
        itemService.createItem(1L, itemDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.confirmedBooking(1L, 1L, true);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(booking.getItem().getId()));
        assertThat(2L, equalTo(booking.getBooker().getId()));
        assertThat(Status.APPROVED, equalTo(booking.getStatus()));
    }

    @Test
    void shouldGetBookingByUserOwner() {
        userService.createUser(userDto);
        userService.createUser(userDto2);
        itemService.createItem(1L, itemDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.confirmedBooking(1L, 1L, true);

        BookingDto getBookingDto = bookingService.getById(1L, 1L);

        assertThat(1L, equalTo(getBookingDto.getItem().getId()));
        assertThat(2L, equalTo(getBookingDto.getBooker().getId()));
        assertThat(Status.APPROVED, equalTo(getBookingDto.getStatus()));
    }

    @Test
    void shouldGetUserBookings() {
        userService.createUser(userDto);
        userService.createUser(userDto2);
        itemService.createItem(1L, itemDto);
        itemService.createItem(2L, itemDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.createBooking(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.createBooking(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.confirmedBooking(1L, 1L, true);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.ALL, 1L, 0, 2);

        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 6L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 5L);
                });
    }

    @Test
    void shouldGetOwnerBookings() {
        userService.createUser(userDto);
        userService.createUser(userDto2);
        itemService.createItem(1L, itemDto);
        itemService.createItem(2L, itemDto);
        bookingService.createBooking(2L, bookingDto);
        bookingService.createBooking(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(3)).build());
        bookingService.createBooking(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.createBooking(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.createBooking(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.createBooking(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.confirmedBooking(1L, 1L, true);
        bookingService.confirmedBooking(2L, 5L, true);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.ALL, 1L, 0, 4);

        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(4)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 3L);
                    Assertions.assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 4L);
                    Assertions.assertThat(list.get(3)).hasFieldOrPropertyWithValue("id", 1L);
                });
    }
}

