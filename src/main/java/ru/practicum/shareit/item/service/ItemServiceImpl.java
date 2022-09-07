package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final String NOT_FOUND = "Не найден предмет ";

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (validateOwner(userId)) {
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(userId);
            return ItemMapper.toItemDto(itemStorage.save(item));
        }
        return null;
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (validateOwner(userId)) {
            Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
            if (item.getOwner() != userId) {
                log.debug("Редактирование доступно только для владельца: {}", itemId);
                throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
            }

            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }

            return ItemMapper.toItemDto(itemStorage.save(item));
        }
        return null;
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId)));
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        if (comments.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream().map(CommentMapper::toCommentDtoResponse).collect(Collectors.toList()));
        }
        addBookings(userId, itemDto);
        return itemDto;
    }

    private ItemDto addBookings(long userId, ItemDto itemDto) {
        if (userId == itemDto.getOwner()) {
            List<BookingForItemDto> bookings = getBooking(itemDto.getId());
            if (bookings.size() > 1) {
                itemDto.setLastBooking(bookings.get(0));
            }
            if (bookings.size() >= 2) {
                itemDto.setNextBooking(bookings.get(1));
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllById(long userId) {
        if (validateOwner(userId)) {
            return itemStorage.findAllByOwnerOrderById(userId).stream().map(ItemMapper::toItemDto).map(i -> this.addBookings(userId, i)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return text.isBlank() ? Collections.emptyList() : itemStorage.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
        if (validateOwner(userId)) {
            List<Booking> booking = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            if (booking == null || booking.stream().filter(b -> b.getItem().getId() == itemId).findFirst().orElse(null) == null) {
                log.debug("Пользователь не бронировал вещь: {}", itemId);
                throw new ValidationException("Пользователь не бронировал вещь.");
            }
            Comment comment = Comment.builder()
                    .author(userStorage.findById(userId).get())
                    .text(commentDto.getText())
                    .item(item)
                    .created(LocalDateTime.now())
                    .build();
            return CommentMapper.toCommentDtoResponse(commentRepository.save(comment));
        }
        return null;
    }

    private boolean validateOwner(long userId) {
        if (userId == 0) {
            log.debug("Не задан владелец.");
            throw new ObjectNotFoundException("Не задан владелец.");
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.debug("Не найден владелец: {}", userId);
            throw new ObjectNotFoundException("Не найден владелец.");
        }
        return true;
    }

    private List<BookingForItemDto> getBooking(long itemId) {
        return bookingRepository.findAllByItem_IdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);
    }

}
