package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
public class BookingCreateRequestDto {
	@NotNull
	private Long itemId;

	@NotNull
	private LocalDateTime start;

	@NotNull
	private LocalDateTime end;
}
