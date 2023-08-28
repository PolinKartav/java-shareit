package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ShareItValidationException;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {
    private ItemService itemService;
    private ItemRepository itemStorage;
    private UserRepository userStorage;
    private CommentRepository commentStorage;
    private ItemRequestRepository requestStorage;

    private static User user;
    private static ItemRequest request;
    private static CreateUpdateItemDto createItemDto;
    private static CreateUpdateItemDto updateItemDto;
    private static CreateUpdateCommentDto createCommentDto;
    private static Item item;
    private static Item updatedItem;
    private static Comment comment;
    private static List<Item> listOfItems;

    @BeforeAll
    static void beforeAll() {
        LocalDateTime currentTime = LocalDateTime.now();

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

        createItemDto = CreateUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();

        updateItemDto = CreateUpdateItemDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(false)
                .build();

        createCommentDto = CreateUpdateCommentDto.builder()
                .text("comment")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .request(request)
                .owner(user)
                .bookings(Set.of())
                .comments(null)
                .build();

        updatedItem = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(false)
                .request(request)
                .request(request)
                .owner(user)
                .bookings(Set.of())
                .comments(null)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        comment = Comment.builder()
                .id(1)
                .text("comment")
                .author(user)
                .created(LocalDateTime.now())
                .build();

        item.setBookings(Set.of(booking));

        listOfItems = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfItems.add(item.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        itemStorage = Mockito.mock(ItemRepository.class);
        userStorage = Mockito.mock(UserRepository.class);
        commentStorage = Mockito.mock(CommentRepository.class);
        requestStorage = Mockito.mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemStorage, userStorage, commentStorage, requestStorage);
    }

    @Test
    void shouldCreateItemWithRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(user.getId(), createItemDto);

        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", createItemDto.getName())
                .hasFieldOrPropertyWithValue("description", createItemDto.getDescription())
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null);
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithCreateWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(item);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(1L, createItemDto)
        );

        assertEquals("Пользоваетль не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithCreateWithNotFoundRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(item);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(1L, createItemDto)
        );

        assertEquals("Запрос не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void shouldUpdateItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        when(itemStorage.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDto = itemService.updateItem(user.getId(), updatedItem.getId(), updateItemDto);

        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", updatedItem.getId())
                .hasFieldOrPropertyWithValue("name", updateItemDto.getName())
                .hasFieldOrPropertyWithValue("description", updateItemDto.getDescription())
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null);
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        when(itemStorage.save(any(Item.class)))
                .thenReturn(updatedItem);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, updateItemDto)
        );

        assertEquals("Такого пользователя нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.save(updatedItem))
                .thenReturn(updatedItem);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, updateItemDto)
        );

        assertEquals("Такого товара нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().owner(user.toBuilder().id(2L).build()).build()));
        when(itemStorage.save(any(Item.class)))
                .thenReturn(updatedItem);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, updateItemDto)
        );

        assertEquals("Такого товара нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void shouldDeleteItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        itemService.removeItem(user.getId(), item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(1L, 1L)
        );

        assertEquals("Такого пользователя нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        doNothing().when(itemStorage).deleteById(anyLong());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(1L, 1L)
        );

        assertEquals("Такого товара нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().owner(user.toBuilder().id(2L).build()).build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.removeItem(1L, 1L)
        );

        assertEquals("Такого товара нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetByIdByOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));

        itemService.getItemById(user.getId(), item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetByIdByNotOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));

        itemService.getItemById(2L, item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionGetByIdByWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, 1L)
        );

        assertEquals("Пользоваетль не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionGetByIdByWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, 1L)
        );

        assertEquals("Такого товара нет.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllByUserIdByOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(listOfItems);

        List<ItemDto> items = itemService.getAllItems(1L, 7, 3);

        assertThat(items)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "itemName");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "itemDescription");
                });
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldSearch() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.search(anyString(), any(Pageable.class)))
                .thenReturn(listOfItems);

        List<ItemDto> items = itemService.search("text", 7, 3);

        assertThat(items)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "itemName");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "itemDescription");
                });
        verify(userStorage, never()).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void shouldCreateComment() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        itemService.createComment(1L, 1L, createCommentDto);

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, times(1)).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, createCommentDto)
        );

        assertEquals("Пользователь не найден.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, createCommentDto)
        );

        assertEquals("вещь не найдена.",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        final ShareItValidationException exception = Assertions.assertThrows(
                ShareItValidationException.class,
                () -> itemService.createComment(2L, 1L, createCommentDto)
        );

        assertEquals("Пользователь с ID = 2 не брал в аренду вещь с ID = 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }
}