package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByOwnerId(Long ownerId);

    Collection<Item> findByRequestId(Long requestId);

    Collection<Item> findByRequestIdIn(Collection<Long> requests);

    @Query(value = "select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "  and i.available=true" +
            "  and length(trim(concat('',?1))) > 0")
    Collection<Item> search(String text);
}
