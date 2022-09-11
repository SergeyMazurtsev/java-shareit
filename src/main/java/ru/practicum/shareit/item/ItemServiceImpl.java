package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CommonGetItemAndUser;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.IdViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final CommonGetItemAndUser commonGetItemAndUser;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto,
                (itemDto.getRequest() != null) ? itemRequestMapper.toRequest(itemDto.getRequest()) : null);
        item.setOwner(commonGetItemAndUser.getInDBUser(userId));
        if (Stream.of(item.getName(), item.getDescription(), item.getAvailable()).anyMatch(Objects::isNull)) {
            throw new ValidatorException("Bad request.");
        }
        if (Stream.of(item.getName(), item.getDescription()).anyMatch(String::isEmpty)) {
            throw new ValidatorException("Bad request.");
        }
        try {
            return convertItem(itemRepository.save(item), userId);
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("Item is already in base.");
        }
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Not found."));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Not found.");
        }
        ItemMapper.patchItem(itemDto, item);
        try {
            return convertItem(itemRepository.save(item), userId);
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("Already have item like this.");
        }
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        try {
            itemRepository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidatorException("Bad request.");
        }
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = commonGetItemAndUser.getInDbItem(itemId);
        return convertItem(item, userId);
    }

    @Override
    public List<ItemDto> getItemsOfUser(Long userId) {
        User user = commonGetItemAndUser.getInDBUser(userId);
        List<Item> items = itemRepository.findAllByOwner(user);
        return items.stream().map(i -> convertItem(i, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Long userId) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text.toLowerCase(), text.toLowerCase());
        return itemList.stream().map(i -> convertItem(i, userId)).collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText() == null) {
            throw new ValidatorException("Bad request1.");
        }
        Item item = commonGetItemAndUser.getInDbItem(itemId);
        User user = commonGetItemAndUser.getInDBUser(userId);
        final var checkApproved = item.getBookings().stream()
                .anyMatch(b-> b.getStatus().equals(BookingStatus.APPROVED)
                        || b.getStatus().equals(BookingStatus.WAITING));
        final var checkFuture = item.getBookings().stream().filter(b->b.getBooker().getId().equals(userId))
                .anyMatch(b->b.getStart().isBefore(LocalDateTime.now()));
        if (!checkApproved || !checkFuture) {
            throw new ValidatorException("Bad request2.");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        try {
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("This comment is already in base.");
        }
    }

    private ItemDto convertItem(Item inItem, Long userId) {
        ItemDto itemDtoOut = ItemMapper.toItemDto(inItem);
        itemDtoOut.setRequest((inItem.getRequest() != null) ? itemRequestMapper.toRequestDto(inItem.getRequest()) : null);
        itemDtoOut.setComments((inItem.getComments() != null) ? inItem.getComments().stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toSet()) : null);

        Booking last = null;
        Booking next = null;
        if (inItem.getBookings() != null) {
            last = inItem.getBookings().stream()
                    .sorted(Comparator.comparing(Booking::getEnd).reversed())
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .findFirst().orElse(null);
            next = inItem.getBookings().stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);
            if (last != null && last.getItem().getOwner().getId() != userId) {
                last = null;
            }
            if (next != null && next.getItem().getOwner().getId() != userId) {
                next = null;
            }
        }
        itemDtoOut.setLastBooking((last != null) ? BookingMapper.bookingShortDto(last) : null);
        itemDtoOut.setNextBooking((next != null) ? BookingMapper.bookingShortDto(next) : null);

        return itemDtoOut;
    }
}
