package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto item);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto findById(long userId, long itemId);

    List<ItemDto> findAllById(long userId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto);
}
