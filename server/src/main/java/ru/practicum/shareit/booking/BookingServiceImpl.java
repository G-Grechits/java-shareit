package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<BookingDtoWithInfo> getBookingsByUserId(long userId, String state, int from, int size) {
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        try {
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByBookerId(userId, pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable)
                            .stream().map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                                    LocalDateTime.now(), pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable)
                            .stream().map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                default:
                    throw new WrongParameterException("Unknown state: " + state);
            }
        } catch (IllegalArgumentException e) {
            throw new WrongParameterException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoWithInfo> getBookingsByItemOwnerId(long itemOwnerId, String state, int from, int size) {
        userService.getUserById(itemOwnerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable);
        if (bookings.isEmpty()) {
            throw new ObjectNotFoundException(
                    String.format("У пользователя с ID = %d нет забронированных вещей.", itemOwnerId));
        }
        try {
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByItemOwnerIdAndEndBefore(itemOwnerId, LocalDateTime.now(),
                                    pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(itemOwnerId,
                                    LocalDateTime.now(), LocalDateTime.now(), pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByItemOwnerIdAndStartAfter(itemOwnerId, LocalDateTime.now(),
                                    pageable).stream()
                            .map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, Status.WAITING, pageable)
                            .stream().map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, Status.REJECTED, pageable)
                            .stream().map(BookingMapper::toBookingDtoWithInfo)
                            .collect(Collectors.toList());
                default:
                    throw new WrongParameterException("Unknown state: " + state);
            }
        } catch (IllegalArgumentException e) {
            throw new WrongParameterException("Unknown state: " + state);
        }
    }

    @Override
    public BookingDtoWithInfo getBookingById(long id, long userId) {
        userService.getUserById(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Бронирование с ID = %d не найдено.", id)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDtoWithInfo(booking);
        }
        throw new AccessDeniedException(
                String.format("Пользователь с ID = %d не является владельцем вещи или автором бронирования.", userId));
    }

    @Override
    public BookingDtoWithInfo createBooking(BookingDto bookingDto, long userId) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new WrongParameterException(
                    "Дата и время окончания бронирования не могут быть раньше даты и времени начала бронирования.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException(String.format(
                        "Вещь с ID = %d не найдена.", bookingDto.getItemId())));
        if (item.getOwner().getId() == userId) {
            throw new AccessDeniedException("Вещь не может быть забронирована её владельцем.");
        }
        if (!item.getAvailable() || bookingRepository.isNotAvailableForBooking(bookingDto.getItemId(),
                bookingDto.getStart(), bookingDto.getEnd())) {
            throw new WrongParameterException(String.format(
                    "Вещь с ID = %d не доступна для бронирования.", item.getId()));
        }
        Booking booking = BookingMapper.toBookingFromDto(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDtoWithInfo(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithInfo approveBooking(long id, long userId, Boolean approved) {
        BookingDtoWithInfo bookingDtoWithInfo = getBookingById(id, userId);
        if (bookingDtoWithInfo.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException(String.format(
                    "Пользователь с ID = %d не является владельцем вещи.", userId));
        }
        if (bookingDtoWithInfo.getStatus().equals(Status.APPROVED)) {
            throw new WrongParameterException("Бронирование не может быть подтверждено дважды.");
        }
        if (approved) {
            bookingDtoWithInfo.setStatus(Status.APPROVED);
        } else {
            bookingDtoWithInfo.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoWithInfo(bookingRepository.save(
                BookingMapper.toBookingFromInfo(bookingDtoWithInfo)));
    }
}
