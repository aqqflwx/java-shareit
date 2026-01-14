package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemWithBookingsMapper {

    public static ItemWithBookingsDto toDto(
            Item item,
            BookingRepository bookingRepository,
            List<Comment> comments) {

        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository.findByItemIdAndStartBeforeAndStatus(
                item.getId(), now, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));

        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.getFirst();
            ItemWithBookingsDto.BookingItemDto lastBookingDto =
                    new ItemWithBookingsDto.BookingItemDto();
            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            dto.setLastBooking(lastBookingDto);
        }

        List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterAndStatus(
                item.getId(), now, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.ASC, "start"));

        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.getFirst();
            ItemWithBookingsDto.BookingItemDto nextBookingDto =
                    new ItemWithBookingsDto.BookingItemDto();
            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            dto.setNextBooking(nextBookingDto);
        }

        List<CommentDto> commentDtos = comments.stream()
                .filter(c -> c.getItem().getId().equals(item.getId()))
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        dto.setComments(commentDtos);

        return dto;
    }
}
