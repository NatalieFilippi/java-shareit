package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestTests {
    @Autowired
    private JacksonTester<BookingRequestDto> json;
    private static LocalDateTime now = LocalDateTime.now();
    private static LocalDateTime later = LocalDateTime.now().plusHours(1);
    private ItemDto item = ItemDto.builder()
            .id(1)
            .name("вещь")
            .description("Описание вещи")
            .owner(1)
            .available(true)
            .request(0)
            .build();
    private static User user = User.builder()
            .id(1)
            .name("Peter")
            .email("peter@ya.ru")
            .build();

    private BookingRequestDto dto = BookingRequestDto.builder()
            .id(1)
            .start(now)
            .end(later)
            .itemDto(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void test() throws IOException {
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(dto.getStart().toString().substring(0, 27));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(dto.getEnd().toString().substring(0, 27));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.APPROVED.toString());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo((int) dto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(dto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(dto.getBooker().getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo((int) dto.getItemDto().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(dto.getItemDto().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(dto.getItemDto().getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.item.owner").isEqualTo((int) dto.getItemDto().getId());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(dto.getItemDto().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo((int) dto.getItemDto().getRequest());
    }
}
