package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.user.dto.GetBookingUserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static GetBookingUserDto booker;
    private static CreateUpdateBookingDto createBookingDto;
    private static BookingDto getBookingDto;
    private static List<BookingDto> listWith20Bookings;

    @BeforeAll
    static void beforeAll() {
        LocalDateTime futureOneDay = LocalDateTime.now().plusDays(1).withNano(0);

        LocalDateTime futureTwoDay = LocalDateTime.now().plusDays(2).withNano(0);

        booker = GetBookingUserDto.builder()
                .id(1L)
                .build();

        GetBookingForItemDto item = GetBookingForItemDto.builder()
                .id(1L)
                .name("item")
                .build();

        createBookingDto = CreateUpdateBookingDto.builder()
                .itemId(1L)
                .start(futureOneDay)
                .end(futureTwoDay)
                .build();

        getBookingDto = BookingDto.builder()
                .id(1L)
                .start(futureOneDay)
                .end(futureTwoDay)
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .build();

        listWith20Bookings = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            listWith20Bookings.add(getBookingDto.toBuilder().id(i + 2L).build());
        }
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithoutHeader() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(anyLong(), any(CreateUpdateBookingDto.class));
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(CreateUpdateBookingDto.class)))
                .thenReturn(getBookingDto);

        String jsonBooking = objectMapper.writeValueAsString(createBookingDto);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).createBooking(booker.getId(), createBookingDto);
    }

    @Test
    void shouldGetExceptionWithApproveBookingWithoutHeader() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).confirmedBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldApproveBooking() throws Exception {
        when(bookingService.confirmedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(getBookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).confirmedBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldRejectBooking() throws Exception {
        when(bookingService.confirmedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(getBookingDto);

        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).confirmedBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getById(anyLong(), anyLong());
    }

    @Test
    void shouldGetBookingWithGetBookingByUserOwner() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(getBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    void shouldGetExceptionWithGetUserBookingsWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingsOfBooker(any(), anyLong());
    }

    @Test
    void shouldGetBookingWithGetUserBookings() throws Exception {
        when(bookingService.getBookingsOfBooker(any(), anyLong()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(bookingService, times(1)).getBookingsOfBooker(any(), anyLong());
    }

    @Test
    void shouldGetExceptionWithGetOwnerBookingsWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingsOfOwner(any(), anyLong());
    }

    @Test
    void shouldGetBookingWithGetOwnerBookings() throws Exception {
        when(bookingService.getBookingsOfOwner(any(), anyLong()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(bookingService, times(1)).getBookingsOfOwner(any(), anyLong());
    }
}