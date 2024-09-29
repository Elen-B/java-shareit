package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getByOwnerId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDto item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentRequestDto commentParamDto) {
        return post("/" + itemId + "/comment", userId, commentParamDto);
    }
}
