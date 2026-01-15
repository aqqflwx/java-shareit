package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;

    @Data
    public static class ItemDto {
        private Long id;
        private String name;
    }

    @Data
    public static class UserDto {
        private Long id;
        private String name;
    }
}
