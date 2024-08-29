package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * API for Item
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable(name = "itemId") Long itemId) {
        System.out.println("getById itemId = " + itemId);
        Item result = itemService.getById(itemId);
        return itemMapper.map(result);
    }

    @GetMapping
    public Collection<ItemDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        Collection<Item> result = itemService.getByOwnerId(userId);
        return result.stream()
                .map(itemMapper::map)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto item) {
        System.out.println("ItemDto = " + item);
        System.out.println("userId = " + userId);
        Item result = itemService.add(itemMapper.map(item, userId));
        return itemMapper.map(result);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "itemId") Long itemId,
                          @Valid @RequestBody ItemUpdateDto item) {
        System.out.println("item = " + item);
        Item result = itemService.update(itemMapper.map(item, itemId, userId));
        return itemMapper.map(result);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(value = "text") String text) {
        Collection<Item> result = itemService.search(text);
        return result.stream()
                .map(itemMapper::map)
                .collect(Collectors.toList());
    }
}
