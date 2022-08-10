package ru.practicum.shareit.item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .description((item.getDescription()))
                .isAvailable(item.isAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }
}
