package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);
    }

    @Test
    void createItem_Success() {
        ItemDto itemDto = new ItemDto(null, "Item", "Description", true, null, null);

        ItemDto result = itemService.createItem(owner.getId(), itemDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateItem_Success() {
        Item item = new Item();
        item.setName("Original");
        item.setDescription("Original description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        ItemDto updateDto = new ItemDto(null, "Updated", "Updated description", false, null, null);
        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void getItemById_Success() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        ItemWithBookingsDto result = itemService.getItemById(owner.getId(), item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Test Item");
    }

    @Test
    void getItemsByOwner_Success() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void searchItems_Success() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Power drill for home use");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<ItemDto> result = itemService.searchItems("drill");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Drill");
    }

    @Test
    void addComment_Success() {
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto commentDto = new CommentCreateDto("Great item!");

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getAuthorName()).isEqualTo("Booker");
    }
}
