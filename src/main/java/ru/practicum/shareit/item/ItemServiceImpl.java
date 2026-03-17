package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

@Service
public class ItemServiceImpl implements ItemService {
	private final ItemDao itemDao;
	private final UserService userService;

	public ItemServiceImpl(ItemDao itemDao, UserService userService) {
		this.itemDao = itemDao;
		this.userService = userService;
	}

	@Override
	public ItemDto create(long ownerId, ItemDto itemDto) {
		if (itemDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is null");
		}
		if (!StringUtils.hasText(itemDto.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is blank");
		}
		if (!StringUtils.hasText(itemDto.getDescription())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description is blank");
		}
		if (itemDto.getAvailable() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item availability is null");
		}

		User owner = userService.getUserOrThrow(ownerId);
		Item item = Item.builder()
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.available(itemDto.getAvailable())
				.owner(owner)
				.build();
		return ItemMapper.toItemDto(itemDao.save(item));
	}

	@Override
	public ItemDto update(long ownerId, long itemId, ItemDto itemDto) {
		Item existing = getExisting(itemId);
		if (itemDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is null");
		}

		if (existing.getOwner() == null || existing.getOwner().getId() == null
				|| existing.getOwner().getId() != ownerId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can edit item");
		}

		if (itemDto.getName() != null) {
			if (!StringUtils.hasText(itemDto.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is blank");
			}
			existing.setName(itemDto.getName());
		}

		if (itemDto.getDescription() != null) {
			if (!StringUtils.hasText(itemDto.getDescription())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description is blank");
			}
			existing.setDescription(itemDto.getDescription());
		}

		if (itemDto.getAvailable() != null) {
			existing.setAvailable(itemDto.getAvailable());
		}

		return ItemMapper.toItemDto(itemDao.save(existing));
	}

	@Override
	public ItemDto getById(long userId, long itemId) {
		userService.getUserOrThrow(userId);
		return ItemMapper.toItemDto(getExisting(itemId));
	}

	@Override
	public List<ItemDto> getAllByOwner(long ownerId) {
		userService.getUserOrThrow(ownerId);
		List<ItemDto> result = new ArrayList<>();
		itemDao.findAll().stream()
				.filter(i -> i.getOwner() != null && i.getOwner().getId() != null && i.getOwner().getId() == ownerId)
				.sorted(Comparator.comparing(Item::getId))
				.map(ItemMapper::toItemDto)
				.forEach(result::add);
		return result;
	}

	@Override
	public List<ItemDto> search(long userId, String text) {
		userService.getUserOrThrow(userId);
		if (!StringUtils.hasText(text)) {
			return List.of();
		}
		String query = text.toLowerCase(Locale.ROOT);

		List<ItemDto> result = new ArrayList<>();
		itemDao.findAll().stream()
				.filter(i -> Boolean.TRUE.equals(i.getAvailable()))
				.filter(i -> containsIgnoreCase(i.getName(), query) || containsIgnoreCase(i.getDescription(), query))
				.sorted(Comparator.comparing(Item::getId))
				.map(ItemMapper::toItemDto)
				.forEach(result::add);
		return result;
	}

	private boolean containsIgnoreCase(String value, String queryLower) {
		if (value == null) {
			return false;
		}
		return value.toLowerCase(Locale.ROOT).contains(queryLower);
	}

	private Item getExisting(long itemId) {
		return itemDao.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
	}
}

