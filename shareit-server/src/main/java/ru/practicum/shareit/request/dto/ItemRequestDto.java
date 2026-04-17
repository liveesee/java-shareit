package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
	private Long id;

	@NotBlank
	private String description;

	private Long requestorId;

	private LocalDateTime created;

	@Builder.Default
	private List<ItemRequestAnswerDto> items = List.of();
}
