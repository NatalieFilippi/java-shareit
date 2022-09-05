package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto create(Item item);

    ItemDto findById(long itemId);

    ItemDto update(Item item, ItemDto itemDto);

    List<ItemDto> findAllById(long userId);

    List<ItemDto> search(String text);
}
