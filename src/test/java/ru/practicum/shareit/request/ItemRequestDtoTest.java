package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private static LocalDateTime time;

    @BeforeAll
    static void beforeAll() {
        time = LocalDateTime.now();
    }

    @Test
    void shouldSerialize() throws IOException {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("requestText")
                .created(time)
                .items(null)
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("requestText");
        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(null);
    }
    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\",\"description\":\"requestText\",\"created\":\"" + time + "\"}";

        ObjectContent<ItemRequestDto> result = json.parse(content);

        assertThat(result).isEqualTo(ItemRequestDto.builder()
                .id(1L)
                .description("requestText")
                .created(time)
                .items(null)
                .build());
    }
}