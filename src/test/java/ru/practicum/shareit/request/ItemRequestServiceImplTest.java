package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
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

class ItemRequestServiceImplTest {
    private ItemRequestService requestService;
    private UserRepository userStorage;
    private ItemRepository itemStorage;
    private ItemRequestRepository requestStorage;

    private static User user;
    private static ItemRequest request;
    private static LocalDateTime currentTime;
    private static CreateUpdateItemRequestDto requestDto;
    private static List<ItemRequest> listOfRequests;

    @BeforeAll
    static void beforeAll() {
        currentTime = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("userName")
                .email("email@ya.ru")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .requester(user)
                .created(currentTime)
                .items(new ArrayList<>())
                .build();

        requestDto = CreateUpdateItemRequestDto.builder()
                .description("request description")
                .build();

        listOfRequests = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfRequests.add(request.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        userStorage = Mockito.mock(UserRepository.class);
        requestStorage = Mockito.mock(ItemRequestRepository.class);
        itemStorage = Mockito.mock(ItemRepository.class);
        requestService = new ItemRequestServiceImpl(requestStorage, userStorage, itemStorage);
    }

    @Test
    void shouldCreateRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto getItemRequestDto = requestService.add(requestDto, user.getId());

        assertThat(getItemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrProperty("created")
                .hasFieldOrPropertyWithValue("items", new ArrayList<>());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void shouldGetExceptionWithCreateRequestNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.save(any(ItemRequest.class)))
                .thenReturn(request);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.add(requestDto, user.getId())
        );

        assertEquals("Пользоваетль не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).save(any(ItemRequest.class));
    }

    @Test
    void shouldGetRequestById() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        ItemRequestDto getItemRequestDto = requestService.getItemRequestById(user.getId(), request.getId());

        assertThat(getItemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrProperty("created")
                .hasFieldOrPropertyWithValue("items", new ArrayList<>());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithRequestByIdNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItemRequestById(user.getId(), request.getId())
        );

        assertEquals("Пользоваетль не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithRequestByIdNotFoundRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItemRequestById(user.getId(), request.getId())
        );

        assertEquals("Запрос не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllRequestsByUserId() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findAllByRequesterIdOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        List<ItemRequestDto> requests = requestService.getUserRequests(user.getId(), 0, 20);

        assertThat(requests)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                        .hasFieldOrProperty("created")
                        .hasFieldOrPropertyWithValue("items", new ArrayList<>()));
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetAllRequestsByUserIdNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findAllByRequesterIdOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getUserRequestsById(user.getId(), 0, 20)
        );

        assertEquals("Пользоваетль не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findAllByRequesterIdOrderByCreatedDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetAllRequests() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findAllByRequesterIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        List<ItemRequestDto> requests = requestService.getUserRequestsById(user.getId(), 7, 3);

        assertThat(requests)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                        .hasFieldOrProperty("created")
                        .hasFieldOrPropertyWithValue("items", new ArrayList<>()));
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetAllRequestsNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findAllByRequesterIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getUserRequests(user.getId(), 7, 3)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }
}