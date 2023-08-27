package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plusHours(1);
    private Sort SORT_BY_START_BY_DESC = Sort.by(Sort.Direction.DESC, "start");
    private Pageable pageable = PageRequest.of(0, 10, SORT_BY_START_BY_DESC);

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().name("user1").email("user1@mail.ru").build();
        user2 = User.builder().name("user2").email("user2@mail.ru").build();
        user3 = User.builder().name("user3").email("user3@mail.ru").build();

        item1 = Item.builder().name("item1").description("iDescr1").available(true)
                .owner(user1).request(null).build();
        item2 = Item.builder().name("item2").description("iDescr2").available(true)
                .owner(user1).request(null).build();
        item3 = Item.builder().name("item3").description("iDescr3").available(true)
                .owner(user2).request(null).build();

        booking1 = Booking.builder().item(item1).booker(user2).status(Status.APPROVED)
                .start(start).end(end).build();
        booking2 = Booking.builder().item(item2).booker(user2).status(Status.APPROVED)
                .start(start.plusMinutes(10)).end(end.plusMinutes(10)).build();
        booking3 = Booking.builder().item(item2).booker(user3).status(Status.APPROVED)
                .start(start.plusMinutes(20)).end(end.plusMinutes(20)).build();
        booking4 = Booking.builder().item(item3).booker(user3).status(Status.APPROVED)
                .start(start.plusMinutes(30)).end(end.plusMinutes(30)).build();

        userRepository.saveAll(List.of(user1, user2, user3));
        itemRepository.saveAll(List.of(item1, item2, item3));
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
    }

    @Test
    void shouldGet2BookingWithFindAllByBookerId() {
        assertEquals(2, bookingRepository.findAllByBookerId(2, pageable).size());
    }

    @Test
    void shouldGet2BookingWithFindAllByBookerIdAndStatus() {
        assertEquals(2, bookingRepository.findAllByBookerIdAndStatus(2,Status.APPROVED, pageable).size());
    }

    @Test
    void shouldGet1BookingWithFindAllByBookerIdAndStartAfter() {
        assertEquals(1, bookingRepository.findAllByBookerIdAndStartAfter(2, LocalDateTime.now(), pageable).size());
    }

    @Test
    void shouldGet1BookingWithFindAllByBookerIdAndEndBefore() {
        assertEquals(1, bookingRepository.findAllByBookerIdAndEndBefore(2, LocalDateTime.now().plusHours(1), pageable).size());
    }

    @Test
    void shouldGet1WithFindAllByOwnerId() {
        assertEquals(1, bookingRepository.findAllByOwnerId(2L, pageable).size());
    }

    @Test
    void shouldGet1WithFindAllByOwnerIdAndStatus() {
        assertEquals(1, bookingRepository.findAllByOwnerIdAndStatus(2L, Status.APPROVED, pageable).size());
    }

    @Test
    void shouldGet1WithFindAllByOwnerIdAndStartAfter() {
        assertEquals(1, bookingRepository.findAllByOwnerIdAndStartAfter(2L, LocalDateTime.now(), pageable).size());
    }

    @Test
    void shouldGet1WithFindAllByOwnerIdAndEndBefore() {
        assertEquals(1, bookingRepository.findAllByOwnerIdAndEndBefore(2L, LocalDateTime.now().plusHours(2), pageable).size());
    }

    @Test
    void shouldGet1WithFindAllByOwnerIdAndStartBeforeAndEndAfter() {
        assertEquals(0, bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(2L, LocalDateTime.now(), pageable).size());
    }

    @Test
    void shouldGet1BookingWithFindAllByBookerIdAndStartBeforeAndEndAfter() {
        LocalDateTime testTime = start.plusMinutes(30);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(), testTime,
                pageable);
        List<Booking> bookingsAll = bookingRepository.findAll();
        assertEquals(4, bookingsAll.size());
        assertEquals(2, bookings.size());
        assertEquals(booking2.getItem().getName(), bookings.get(0).getItem().getName());

        bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(), testTime,
                PageRequest.of(0, 1, SORT_BY_START_BY_DESC));

        assertEquals(1, bookings.size());
        assertEquals(booking2.getItem().getName(), bookings.get(0).getItem().getName());
        bookings = bookingRepository.findAllByOwnerId(user1.getId(), pageable);
        assertEquals(3, bookings.size());
        assertEquals(booking3.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking3.getId(), bookings.get(0).getId());
    }
}
