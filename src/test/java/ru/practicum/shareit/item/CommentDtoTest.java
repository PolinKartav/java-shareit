package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private static LocalDateTime time;

    @BeforeAll
    static void beforeAll() {
        time = LocalDateTime.now();
    }

    @Test
    void shouldSerialize() throws IOException {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("commentText")
                .created(time)
                .authorName("author")
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).hasJsonPathStringValue("$.authorName");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("commentText");
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"id\":\"1\",\"text\":\"commentText\",\"created\":\"" +
                time +
                "\",\"authorName\":\"author\"}";

        ObjectContent<CommentDto> result = json.parse(content);

        assertThat(result).isEqualTo(CommentDto.builder()
                .id(1L)
                .text("commentText")
                .created(time)
                .authorName("author")
                .build());
    }
}