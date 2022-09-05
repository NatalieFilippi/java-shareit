package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto findById(long itemId);

    List<ItemDto> findAllById(long userId);

    List<ItemDto> search(String text);
}
