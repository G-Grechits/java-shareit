package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemRequestDtoWithInfo> getItemRequestsByUserId(long userId) {
        userService.getUserById(userId);
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream().map(ItemRequestMapper::toItemRequestDtoWithInfo)
                .collect(Collectors.toList());
        itemRequests.forEach(this::setItemsForRequest);
        return itemRequests;
    }

    @Override
    public List<ItemRequestDtoWithInfo> getOtherItemRequests(long userId, int from, int size) {
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestRepository.findAll(pageable).stream()
                .filter(i -> i.getRequester().getId() != userId)
                .map(ItemRequestMapper::toItemRequestDtoWithInfo)
                .collect(Collectors.toList());
        itemRequests.forEach(this::setItemsForRequest);
        return itemRequests;
    }

    @Override
    public ItemRequestDtoWithInfo getItemRequestById(long id, long userId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Запрос с ID = %d не найден.", id)));
        ItemRequestDtoWithInfo itemRequestDtoWithInfo = ItemRequestMapper.toItemRequestDtoWithInfo(itemRequest);
        setItemsForRequest(itemRequestDtoWithInfo);
        return itemRequestDtoWithInfo;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private void setItemsForRequest(ItemRequestDtoWithInfo itemRequest) {
        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        if (!items.isEmpty()) {
            itemRequest.setItems(items);
        }
    }
}
