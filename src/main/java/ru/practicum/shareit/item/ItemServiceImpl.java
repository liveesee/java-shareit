package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;
	private final UserService userService;

	@Override
	public ItemDto create(long ownerId, ItemCreateRequestDto itemDto) {
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

		Item item = ItemMapper.toItem(itemDto);
		item.setOwner(userService.getUserOrThrow(ownerId));
		return ItemMapper.toItemDto(itemRepository.save(item));
	}

	@Override
	public ItemDto update(long ownerId, long itemId, ItemUpdateRequestDto itemDto) {
		if (itemDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is null");
		}

		Item existing = getExisting(itemId);

		if (existing.getOwner() == null || existing.getOwner().getId() == null
				|| existing.getOwner().getId() != ownerId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can edit item");
		}

		if (itemDto.getName() != null) {
			if (!StringUtils.hasText(itemDto.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is blank");
			}
		}

		if (itemDto.getDescription() != null) {
			if (!StringUtils.hasText(itemDto.getDescription())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description is blank");
			}
		}

		ItemMapper.updateItem(existing, itemDto);
		return ItemMapper.toItemDto(itemRepository.save(existing));
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDto getById(long userId, long itemId) {
		userService.getUserOrThrow(userId);
		return toItemDto(getExisting(itemId), userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OwnerItemDto> getAllByOwner(long ownerId) {
		userService.getUserOrThrow(ownerId);
		return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).stream()
				.map(this::toOwnerItemDto)
				.toList();
	}

	@Override
	public CommentDto addComment(long userId, long itemId, CommentCreateRequestDto commentDto) {
		if (commentDto == null || !StringUtils.hasText(commentDto.getText())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment text is blank");
		}

		User author = userService.getUserOrThrow(userId);
		Item item = getExisting(itemId);
		boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
				userId, itemId, Status.APPROVED, LocalDateTime.now());

		if (!hasCompletedBooking) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not completed booking for this item");
		}

		Comment comment = ItemMapper.toComment(commentDto);
		comment.setAuthor(author);
		comment.setItem(item);
		comment.setCreated(LocalDateTime.now());

		return ItemMapper.toCommentDto(commentRepository.save(comment));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> search(long userId, String text) {
		userService.getUserOrThrow(userId);
		if (!StringUtils.hasText(text)) {
			return List.of();
		}
		return itemRepository.searchAvailableByText(text).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Transactional(readOnly = true)
	private Item getExisting(long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
	}

	private OwnerItemDto toOwnerItemDto(Item item) {
		OwnerItemDto dto = ItemMapper.toOwnerItemDto(item);
		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());

		LocalDateTime lastBooking = bookings.stream()
				.filter(booking -> !booking.getStart().isAfter(now))
				.map(Booking::getStart)
				.max(LocalDateTime::compareTo)
				.orElse(null);

		LocalDateTime nextBooking = bookings.stream()
				.filter(booking -> booking.getStart().isAfter(now))
				.map(Booking::getStart)
				.min(LocalDateTime::compareTo)
				.orElse(null);

		dto.setLastBooking(lastBooking);
		dto.setNextBooking(nextBooking);
		dto.setComments(getComments(item.getId()));
		return dto;
	}

	private ItemDto toItemDto(Item item, long userId) {
		ItemDto dto = ItemMapper.toItemDto(item);
		if (item.getOwner() != null && item.getOwner().getId() != null && item.getOwner().getId().equals(userId)) {
			enrichWithBookingDates(dto, item.getId());
		}
		dto.setComments(getComments(item.getId()));
		return dto;
	}

	private void enrichWithBookingDates(ItemDto dto, Long itemId) {
		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(itemId);

		LocalDateTime lastBooking = bookings.stream()
				.filter(booking -> !booking.getStart().isAfter(now))
				.map(Booking::getStart)
				.max(LocalDateTime::compareTo)
				.orElse(null);

		LocalDateTime nextBooking = bookings.stream()
				.filter(booking -> booking.getStart().isAfter(now))
				.map(Booking::getStart)
				.min(LocalDateTime::compareTo)
				.orElse(null);

		dto.setLastBooking(lastBooking);
		dto.setNextBooking(nextBooking);
	}

	private List<CommentDto> getComments(Long itemId) {
		return commentRepository.findAllByItemIdOrderByCreatedAsc(itemId).stream()
				.map(ItemMapper::toCommentDto)
				.toList();
	}
}
