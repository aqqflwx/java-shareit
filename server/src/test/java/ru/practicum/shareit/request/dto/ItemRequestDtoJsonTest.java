package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serialize_Success() throws Exception {
        ItemForRequestDto itemDto = new ItemForRequestDto(1L, "Item", 2L);
        ItemRequestDto dto = new ItemRequestDto(
                1L,
                "Need a drill",
                LocalDateTime.of(2024, 1, 15, 10, 0, 0),
                List.of(itemDto)
        );

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
    }

    @Test
    void deserialize_Success() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2024-01-15T10:00:00\"," +
                "\"items\":[{\"id\":1,\"name\":\"Item\",\"ownerId\":2}]}";

        ItemRequestDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        assertThat(result.getItems()).hasSize(1);
    }
}
