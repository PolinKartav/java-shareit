package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ShareItValidationException;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.GetBookingUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    private BookingService bookingService;
    private BookingRepository bookingStorage;
    private ItemRepository itemStorage;
    private UserRepository userStorage;

    private static User user;
    private static User user2;
    private static Item item;
    private static CreateUpdateBookingDto bookingDto;
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;
    private static GetBookingUserDto booker;
    private static Booking booking;
    private static GetBookingForItemDto itemDto;
    private static List<Booking> listOfBookings;

    @BeforeAll
    static void beforeAll() {
        startTime = LocalDateTime.now().minusDays(2);

        endTime = LocalDateTime.now().minusDays(1);

        user = User.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("mail2@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        bookingDto = CreateUpdateBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build();

        booker = GetBookingUserDto.builder()
                .id(2L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(startTime)
                .end(endTime)
                .booker(user2)
                .status(Status.WAITING)
                .item(item)
                .build();

        itemDto = GetBookingForItemDto.builder()
                .id(1L)
                .name("itemName")
                .build();

        listOfBookings = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfBookings.add(booking.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        bookingStorage = Mockito.mock(BookingRepository.class);
        itemStorage = Mockito.mock(ItemRepository.class);
        userStorage = Mockito.mock(UserRepository.class);
        bookingService = new BookingServiceImpl(bookingStorage, userStorage, itemStorage);
    }

    @Test
    void shouldCreateBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto getBookingDto = bookingService.createBooking(2L, bookingDto);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingDto)
        );

        assertEquals("User with id 2 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingDto)
        );

        assertEquals("Item with id 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotAvailableException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item.toBuilder().available(false).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final ShareItValidationException exception = Assertions.assertThrows(
                ShareItValidationException.class,
                () -> bookingService.createBooking(2L, bookingDto)
        );

        assertEquals("Бронирование недоступно",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundSelfItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto)
        );

        assertEquals("Владелей вещи не может забронировать свою вещь.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldApproveBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking.toBuilder().status(Status.APPROVED).build());

        BookingDto getBookingDto = bookingService.confirmedBooking(1L, 1L, true);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.APPROVED)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldRejectBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking.toBuilder().status(Status.REJECTED).build());

        BookingDto getBookingDto = bookingService.confirmedBooking(1L, 1L, false);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.REJECTED)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.confirmedBooking(1L, 1L, true)
        );

        assertEquals("User with id 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.confirmedBooking(1L, 1L, true)
        );

        assertEquals("Нет данных о бронирование.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.confirmedBooking(2L, 1L, true)
        );

        assertEquals("Бронирование не найдено.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNotAvailableAlreadyApproved() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().status(Status.APPROVED).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final ShareItValidationException exception = Assertions.assertThrows(
                ShareItValidationException.class,
                () -> bookingService.confirmedBooking(1L, 1L, true)
        );

        assertEquals("Бронирование недоступно.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        BookingDto getBookingDto = bookingService.getById(1L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        BookingDto getBookingDto = bookingService.getById(2L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, 1L)
        );

        assertEquals("User with id 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, 1L)
        );

        assertEquals("Нет данных о бронирование.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(3L, 1L)
        );

        assertEquals("Бронирование не найдено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetUserBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.ALL, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetUserBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsOfBooker(State.ALL, 1L, 7, 3)
        );

        assertEquals("User with id 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findAllByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithCurrent() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.CURRENT, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfter(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithPast() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.PAST, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByBookerIdAndEndBefore(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithFuture() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.FUTURE, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByBookerIdAndStartAfter(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithWaiting() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.WAITING, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByBookerIdAndStatus(
                        anyLong(),
                        any(Status.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithReject() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfBooker(State.REJECTED, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByBookerIdAndStatus(
                        anyLong(),
                        any(Status.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.ALL, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetOwnerBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsOfOwner(State.ALL, 1L, 7, 3)
        );

        assertEquals("User with id 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithCurrent() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.CURRENT, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByOwnerIdAndStartBeforeAndEndAfter(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithFuture() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.FUTURE, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByOwnerIdAndStartAfter(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithWaiting() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.WAITING, 1L, 7, 3);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1))
                .findAllByOwnerIdAndStatus(anyLong(),
                        any(Status.class),
                        any(Pageable.class));
    }
}