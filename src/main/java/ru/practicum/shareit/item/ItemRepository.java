package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long generatedId = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(generatedId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
