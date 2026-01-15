package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_Success() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 16, 10, 0, 0));
        dto.setStatus(BookingStatus.APPROVED);

        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        dto.setItem(itemDto);

        BookingDto.UserDto userDto = new BookingDto.UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        dto.setBooker(userDto);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-16T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
    }

    @Test
    void deserialize_Success() throws Exception {
        String jsonContent = "{\"id\":1,\"start\":\"2024-01-15T10:00:00\",\"end\":\"2024-01-16T10:00:00\"," +
                "\"status\":\"APPROVED\",\"item\":{\"id\":1,\"name\":\"Item\"},\"booker\":{\"id\":1,\"name\":\"User\"}}";

        BookingDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
