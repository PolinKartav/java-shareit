package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateUpdateItemRequestDtoTest {
    @Autowired
    private JacksonTester<CreateUpdateItemRequestDto> json;

    @Test
    void shouldSerialize() throws IOException {
        CreateUpdateItemRequestDto dto = CreateUpdateItemRequestDto.builder()
                .description("requestText")
                .build();

        JsonContent<CreateUpdateItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("requestText");
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"description\":\"requestText\"}";

        ObjectContent<CreateUpdateItemRequestDto> result = json.parse(content);

        assertThat(result).isEqualTo(CreateUpdateItemRequestDto.builder()
                .description("requestText")
                .build());
    }
}