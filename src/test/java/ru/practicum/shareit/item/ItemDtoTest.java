package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void shouldSerialize() throws IOException {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(false)
                .requestId(4L)
                .nextBooking(null)
                .lastBooking(null)
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathNumberValue("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("itemDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(4);
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\",\"name\":\"itemName\",\"description\":\"itemDescription\"" +
                ",\"available\":\"false\"}";

        ObjectContent<ItemDto> result = json.parse(content);

        assertThat(result).isEqualTo(ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(false)
                .comments(null)
                .nextBooking(null)
                .lastBooking(null)
                .build());
    }
}