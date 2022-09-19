package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestTests {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private final static LocalDateTime now = LocalDateTime.now();
    private static ItemRequestDto dto = new ItemRequestDto(1L,
            "Запрос",
            1L,
            now,
            List.of(ItemDto.builder()
                    .id(1)
                    .name("вещь")
                    .description("Описание вещи")
                    .owner(1)
                    .available(true)
                    .request(0)
                    .build()));

    @Test
    void test() throws IOException {
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestor");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString().substring(0, 27));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo((int) dto.getItems().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(dto.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(dto.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].owner").isEqualTo((int) dto.getItems().get(0).getOwner());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo((dto.getItems().get(0).getAvailable()));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo((int) dto.getItems().get(0).getRequest());
    }

}
