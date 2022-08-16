package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class ItemStorageImpl implements ItemStorage {
    private static HashMap<Long, List<Item>> items = new HashMap<>();
    private long lastItemId = 0;

    @Override
    public ItemDto create(Item item) {
        item.setId(getNextId());
        items.compute(item.getOwner(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(long itemId) {
        List<Item> item = items.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        Item foundItem = item.stream().filter(i -> i.getId() == itemId).findFirst().orElseGet(() -> null);
        return ItemMapper.toItemDto(foundItem);
    }

    @Override
    public ItemDto update(Item item, Map<String, String> fields) {
        Item itemCopy = new Item(item);
        fields.remove("id");
        fields.forEach((key, value) -> {
            if (key.equals("available")) {
                key = "isAvailable";
            }
            Field field = ReflectionUtils.findField(Item.class, (String) key);
            field.setAccessible(true);
            if (field.getType() == boolean.class) {
                ReflectionUtils.setField(field, itemCopy, Boolean.parseBoolean(value));
            } else {
                ReflectionUtils.setField(field, itemCopy, value);
            }
        });

        items.get(item.getOwner()).remove(ItemMapper.toItem(findById(item.getId())));
        items.get(item.getOwner()).add(itemCopy);
        log.debug("Обновлена вещь: {}", itemCopy.toString());
        return ItemMapper.toItemDto(itemCopy);
    }

    @Override
    public List<ItemDto> findAllById(long userId) {
        return new ArrayList<>(items.get(userId).stream().map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList()));
    }

    @Override
    public List<ItemDto> search(String text) {
        List<Item> item = items.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        List<ItemDto> foundItem = item.stream()
                .filter(i ->i.isAvailable())
                .filter(i -> (i.getDescription().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text)))
                .map(i -> ItemMapper.toItemDto(i))
                .collect(Collectors.toList());
        return foundItem;
    }

    private long getNextId() {
        return ++lastItemId;
    }

}
