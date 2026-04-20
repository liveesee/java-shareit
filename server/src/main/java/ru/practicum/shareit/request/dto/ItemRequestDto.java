package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
	private Long id;

	private String description;

	private Long requestorId;

	private LocalDateTime created;

	@Builder.Default
	private List<ItemRequestAnswerDto> items = List.of();
}
