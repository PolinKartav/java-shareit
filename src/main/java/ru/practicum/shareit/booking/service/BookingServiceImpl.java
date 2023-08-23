package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ShareItValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(Long userId, CreateUpdateBookingDto createUpdateBookingDto) {
        User user = getUserById(userId);

        Item item = getItemById(createUpdateBookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелей вещи не может забронировать свою вещь.");
        }

        LocalDateTime start = createUpdateBookingDto.getStart();
        LocalDateTime end = createUpdateBookingDto.getEnd();

        if (!end.isAfter(start)) {
            throw new ShareItValidationException("Неправильное время бронирования.");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ShareItValidationException("Бронирование недоступно");
        }

        Booking booking = BookingMapper.toBookingFromCreateUpdateBookingDto(createUpdateBookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDtoFromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto confirmedBooking(Long userId, Long bookingId, boolean approved) {
        User user = getUserById(userId);
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("Бронирование не найдено.");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ShareItValidationException("Бронирование недоступно.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingDtoFromBooking(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Бронирование не найдено");
        }

        return BookingMapper.toBookingDtoFromBooking(booking);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsOfBooker(State state, Long bookerId, int from, int size) {
        getUserById(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, PageRequest.of(from / size, size, sort));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING, PageRequest.of(from / size, size, sort));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED, PageRequest.of(from / size, size, sort));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsOfOwner(State state, Long ownerId, int from, int size) {
        getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING, PageRequest.of(from / size, size, sort));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, PageRequest.of(from / size, size, sort));
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(), PageRequest.of(from / size, size, sort));
                break;
            default:
                bookings = bookingRepository.findAllByOwnerId(ownerId, PageRequest.of(from / size, size, sort));
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Нет данных о бронирование."));
    }
}
