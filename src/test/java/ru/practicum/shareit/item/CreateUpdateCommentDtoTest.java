package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateUpdateCommentDtoTest {
    @Autowired
    private JacksonTester<CreateUpdateCommentDto> json;

    @Test
    void shouldSerialize() throws IOException {
        CreateUpdateCommentDto dto = CreateUpdateCommentDto.builder()
                .text("text")
                .build();

        JsonContent<CreateUpdateCommentDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"text\":\"commentText\"}";

        ObjectContent<CreateUpdateCommentDto> result = json.parse(content);

        assertThat(result).isEqualTo(CreateUpdateCommentDto.builder()
                .text("commentText")
                .build());
    }
}