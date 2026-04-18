package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(long userId, long requestId);

    List<ItemRequestDto> getAll(long userId);

    List<ItemRequestDto> getOwnerRequests(long userId);
}
