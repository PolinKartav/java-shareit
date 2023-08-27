package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.dto.CreateUpdateBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CreateUpdateBookingDtoTest {
    @Autowired
    private JacksonTester<CreateUpdateBookingDto> json;
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;

    @BeforeAll
    static void beforeAll() {
        startTime = LocalDateTime.now().minusDays(2);
        endTime = LocalDateTime.now().minusDays(1);
    }

    @Test
    void shouldSerialize() throws IOException {
        CreateUpdateBookingDto dto = CreateUpdateBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build();

        JsonContent<CreateUpdateBookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void shouldDeserialize() throws IOException {
        String content = "{\"itemId\":\"1\",\"start\":\"" + startTime + "\",\"end\":\"" + endTime + "\"}";

        ObjectContent<CreateUpdateBookingDto> result = json.parse(content);

        assertThat(result).isEqualTo(CreateUpdateBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build());
    }
}