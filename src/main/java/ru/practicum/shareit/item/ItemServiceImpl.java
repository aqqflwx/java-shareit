package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        // Добавляем lastBooking и nextBooking только для владельца
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> lastBookings = bookingRepository.findByItemIdAndStartBeforeAndStatus(
                    itemId, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
            if (!lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.getFirst();
                ItemWithBookingsDto.BookingItemDto lastBookingDto = new ItemWithBookingsDto.BookingItemDto();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setBookerId(lastBooking.getBooker().getId());
                dto.setLastBooking(lastBookingDto);
            }

            List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterAndStatus(
                    itemId, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
            if (!nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.getFirst();
                ItemWithBookingsDto.BookingItemDto nextBookingDto = new ItemWithBookingsDto.BookingItemDto();
                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setBookerId(nextBooking.getBooker().getId());
                dto.setNextBooking(nextBookingDto);
            }
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        return items.stream()
                .map(item -> {
                    ItemWithBookingsDto dto = new ItemWithBookingsDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setDescription(item.getDescription());
                    dto.setAvailable(item.getAvailable());

                    LocalDateTime now = LocalDateTime.now();
                    Sort sort = Sort.by(Sort.Direction.DESC, "start");

                    List<Booking> lastBookings = bookingRepository.findByItemIdAndStartBeforeAndStatus(
                            item.getId(), now, BookingStatus.APPROVED, sort);
                    if (!lastBookings.isEmpty()) {
                        Booking lastBooking = lastBookings.get(0);
                        ItemWithBookingsDto.BookingItemDto lastBookingDto =
                                new ItemWithBookingsDto.BookingItemDto();
                        lastBookingDto.setId(lastBooking.getId());
                        lastBookingDto.setBookerId(lastBooking.getBooker().getId());
                        dto.setLastBooking(lastBookingDto);
                    }

                    Sort sortAsc = Sort.by(Sort.Direction.ASC, "start");
                    List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterAndStatus(
                            item.getId(), now, BookingStatus.APPROVED, sortAsc);
                    if (!nextBookings.isEmpty()) {
                        Booking nextBooking = nextBookings.get(0);
                        ItemWithBookingsDto.BookingItemDto nextBookingDto =
                                new ItemWithBookingsDto.BookingItemDto();
                        nextBookingDto.setId(nextBooking.getId());
                        nextBookingDto.setBookerId(nextBooking.getBooker().getId());
                        dto.setNextBooking(nextBookingDto);
                    }

                    List<CommentDto> itemComments = comments.stream()
                            .filter(comment -> comment.getItem().getId().equals(item.getId()))
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    dto.setComments(itemComments);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(
                userId, now, Sort.unsorted());

        boolean hasBookedItem = bookings.stream()
                .anyMatch(booking -> booking.getItem().getId().equals(itemId));

        if (!hasBookedItem) {
            throw new IllegalArgumentException("User must have completed booking to add comment");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }
}
