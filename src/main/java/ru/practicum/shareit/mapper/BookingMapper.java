package ru.practicum.shareit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.GetBookingForGetItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDtoFromBooking(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toGetBookingForItemDtoFromItem(booking.getItem()))
                .booker(UserMapper.toGetBookingUserDtoFromUser(booking.getBooker()))
                .build();
    }

    public Booking toBookingFromCreateUpdateBookingDto(CreateUpdateBookingDto createUpdateBookingDto) {
        return Booking.builder()
                .start(createUpdateBookingDto.getStart())
                .end(createUpdateBookingDto.getEnd())
                .build();
    }

    public GetBookingForGetItemDto toGetBookingForItemDtoFromBooking(Booking booking) {
        if (booking == null) {
            return null;
        }

        return GetBookingForGetItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}



