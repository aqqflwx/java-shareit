    package ru.practicum.shareit.item;

    import ru.practicum.shareit.item.dto.ItemDto;
    import ru.practicum.shareit.item.model.Item;

    import java.util.ArrayList;

    public class ItemMapper {
        public static ItemDto toItemDto(Item item) {
            ItemDto dto = new ItemDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());
            dto.setRequestId(item.getRequestId());
            dto.setComments(new ArrayList<>());
            return dto;
        }

        public static Item toItem(ItemDto itemDto) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            return item;
        }
    }
