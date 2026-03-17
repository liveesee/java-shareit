package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
	public static final String STATUS_WAITING = "WAITING";
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_REJECTED = "REJECTED";
	public static final String STATUS_CANCELED = "CANCELED";

	private Long id;
	private LocalDateTime start;
	private LocalDateTime end;
	private Item item;
	private User booker;
	private String status;
}
