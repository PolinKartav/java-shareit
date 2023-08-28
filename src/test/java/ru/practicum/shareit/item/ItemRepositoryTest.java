package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;
    private Item item5;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;
    private static ItemRequest itemRequest1;
    private static ItemRequest itemRequest2;
    private static ItemRequest itemRequest3;
    private static ItemRequest itemRequest4;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        user1 = User.builder().name("user1").email("user1@mail.ru").build();
        user2 = User.builder().name("user2").email("user2@mail.ru").build();
        user3 = User.builder().name("user3").email("user3@mail.ru").build();
        user4 = User.builder().name("user4").email("user4@mail.ru").build();
        user5 = User.builder().name("user5").email("user5@mail.ru").build();

        item1 = Item.builder().name("iteMs1").description("Description1").available(true)
                .owner(user1).request(null).build();
        item2 = Item.builder().name("item2").description("Description2").available(false)
                .owner(user2).request(null).build();
        item3 = Item.builder().name("iteM3").description("Description3").available(true)
                .owner(user3).request(null).build();
        item4 = Item.builder().name("yTem4").description("Description4").available(true)
                .owner(user4).request(null).build();
        item5 = Item.builder().name("Table5").description("Description itEm5").available(true)
                .owner(user5).request(null).build();

        itemRequest1 = ItemRequest.builder()
                .description("description1").requester(user1).created(now.plusSeconds(10)).build();
        itemRequest2 = ItemRequest.builder()
                .description("description2").requester(user1).created(now.plusSeconds(20)).build();
        itemRequest3 = ItemRequest.builder()
                .description("description3").requester(user2).created(now.plusSeconds(30)).build();
        itemRequest4 = ItemRequest.builder()
                .description("description4").requester(user1).created(now.minusSeconds(40)).build();

        userRepository.saveAll(List.of(user1, user2, user3, user4, user5));
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        itemRequestRepository.saveAll(List.of(itemRequest1, itemRequest2, itemRequest3, itemRequest4));
    }

    @Test
    void shouldFindAll() {
        assertEquals(5, itemRepository.findAll().size());
    }
}







