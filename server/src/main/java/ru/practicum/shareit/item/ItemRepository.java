package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description,
                                                                                  Pageable pageable);
}
