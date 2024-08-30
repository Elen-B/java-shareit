package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * API for Item
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable(name = "itemId") Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByOwnerId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto item) {
        return itemService.add(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "itemId") Long itemId,
                          @Valid @RequestBody ItemUpdateDto item) {
        return itemService.update(item, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String text) {
        return itemService.search(text);
    }
}
