package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(new ArrayList<>());
        return dto;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = toItemRequestDto(request);
        if (items != null) {
            dto.setItems(items.stream()
                    .map(ItemRequestMapper::toItemForRequestDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        ItemForRequestDto dto = new ItemForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}
