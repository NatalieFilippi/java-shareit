package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (validateOwner(userId)) {
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(userId);
            return itemStorage.create(item);
        }
        return null;
    }

    @Override
    public ItemDto update(long userId, long itemId, Map<String, String> fields) {
        if (validateOwner(userId)) {
            Item item = ItemMapper.toItem(itemStorage.findById(itemId));
            if (item.getOwner() != userId) {
                log.debug("Редактирование доступно только для владельца: {}", itemId);
                throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
            }
            return itemStorage.update(item, fields);
        }
        return null;
    }

    @Override
    public ItemDto findById(long itemId) {
        return itemStorage.findById(itemId);
    }

    @Override
    public List<ItemDto> findAllById(long userId) {
        if (validateOwner(userId)) {
            return itemStorage.findAllById(userId);
        }
        return Collections.emptyList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return text.isBlank() ? Collections.emptyList() : itemStorage.search(text.toLowerCase());

    }

    private boolean validateOwner(long userId) {
        if (userId == 0) {
            log.debug("Не задан владелец.");
            throw new ObjectNotFoundException("Не задан владелец.");
        }
        if (userStorage.findById(userId) == null) {
            log.debug("Не найден владелец: {}", userId);
            throw new ObjectNotFoundException("Не найден владелец.");
        }
        return true;
    }

}
