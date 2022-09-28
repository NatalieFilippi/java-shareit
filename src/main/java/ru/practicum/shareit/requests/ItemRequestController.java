package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllById(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("requestId") long requestId) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "20") int size) {
        return requestService.getAll(userId, from, size);
    }

}
