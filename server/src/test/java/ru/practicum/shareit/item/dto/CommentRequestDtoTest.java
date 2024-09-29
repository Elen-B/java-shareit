package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRequestDtoTest {
    private final JacksonTester<CommentRequestDto> json;

    @Test
    void commentRequestDtoTest() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto("new comment");
        JsonContent<CommentRequestDto> result = json.write(commentRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentRequestDto.getText());
    }
}