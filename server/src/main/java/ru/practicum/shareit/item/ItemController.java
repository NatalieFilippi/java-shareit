package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable("itemId") long itemId,
                          @RequestBody ItemDto itemDto) throws ObjectNotFoundException {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable("itemId") long itemId) throws ObjectNotFoundException {
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(required = false, defaultValue = "0") int from,
                                     @RequestParam(required = false, defaultValue = "20") int size) {
        return itemService.findAllById(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam String text,
                                @RequestParam(required = false, defaultValue = "0") int from,
                                @RequestParam(required = false, defaultValue = "20") int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemId") long itemId,
                                         @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
