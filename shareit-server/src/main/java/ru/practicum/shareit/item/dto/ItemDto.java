package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
	private Long id;
	private String name;
	private String description;
	private Boolean available;
	private User owner;
	private LocalDateTime lastBooking;
	private LocalDateTime nextBooking;
	private List<CommentDto> comments;
}
