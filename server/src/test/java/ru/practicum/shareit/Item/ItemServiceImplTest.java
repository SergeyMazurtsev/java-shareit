package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);

    private ItemService itemService = new ItemServiceImpl(itemRepository, commentRepository, commonService);

    private Item item;
    private ItemDto itemDto;
    private User user;
    private Comment comment;
    private CommentDto commentDto;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        user = User.builder().id(3L).name("User3").build();
        comment = Comment.builder()
                .id(1L)
                .text("Test comments")
                .author(user)
                .created(time)
                .build();
        commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getName())
                .created(time)
                .build();
        item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Testing")
                .available(true)
                .request(ItemRequest.builder().id(1L).build())
                .comments(Set.of(comment))
                .owner(user)
                .bookings(Set.of(
                        Booking.builder().id(1L).booker(user).start(LocalDateTime.now().minusMinutes(10))
                                .end(LocalDateTime.now().minusMinutes(5)).item(Item.builder().owner(user).build())
                                .status(BookingStatus.APPROVED).build(),
                        Booking.builder().id(2L).booker(user).start(LocalDateTime.now().minusMinutes(5))
                                .end(LocalDateTime.now().plusMinutes(10)).item(Item.builder().owner(user).build())
                                .status(BookingStatus.WAITING).build()))
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .comments(Set.of(commentDto))
                .lastBooking(BookingShortDto.builder().id(1L).bookerId(user.getId()).build())
                .nextBooking(BookingShortDto.builder().id(2L).bookerId(user.getId()).build())
                .build();
        comment.setItem(item);
    }

    @Test
    void createItem() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto testItemDto = itemService.createItem(itemDto, user.getId());
        assertThat(testItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(testItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(testItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(testItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(testItemDto.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(testItemDto.getComments(), equalTo(itemDto.getComments()));
        assertThat(testItemDto.getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(testItemDto.getNextBooking(), equalTo(itemDto.getNextBooking()));

        ItemDto itemDto1 = itemDto;
        itemDto1.setName(null);
        try {
            ItemDto testItemDto1 = itemService.createItem(itemDto1, user.getId());
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Name or description or available is null."));
        }

        ItemDto itemDto2 = itemDto;
        itemDto2.setName("");
        try {
            ItemDto testItemDto1 = itemService.createItem(itemDto2, user.getId());
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Name or description or available is empty."));
        }

        verify(commonService, times(3))
                .getInDBUser(anyLong());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void patchItem() {
        itemDto.setName("Test2");
        item.setName("Test2");
        when(commonService.getInDbItem(anyLong()))
                .thenReturn(item);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto testItemDto = itemService.patchItem(itemDto, item.getId(), user.getId());
        assertThat(testItemDto.getName(), equalTo(itemDto.getName()));

        try {
            ItemDto testItemDto1 = itemService.patchItem(itemDto, item.getId(), 100L);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("User id and owner of item id is not equal."));
        }

        verify(commonService, times(2))
                .getInDbItem(anyLong());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void deleteItem() {
        doNothing().when(itemRepository).deleteById(anyLong());
        itemService.deleteItem(item.getId(), user.getId());
        verify(itemRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDto testItemDto = itemService.getItem(1L, user.getId());
        assertThat(testItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(testItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(testItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(testItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(testItemDto.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(testItemDto.getComments(), equalTo(itemDto.getComments()));
        assertThat(testItemDto.getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(testItemDto.getNextBooking(), equalTo(itemDto.getNextBooking()));

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getItemsOfUser() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of(item);
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(itemRepository.findAllByOwner(any(User.class), any(Pageable.class)))
                .thenReturn(items);

        List<ItemDto> itemDtos = List.of(itemDto);
        List<ItemDto> testItemDto = itemService.getItemsOfUser(user.getId(), 0, 10);
        assertThat(testItemDto, hasSize(itemDtos.size()));
        for (ItemDto i : itemDtos) {
            assertThat(testItemDto, hasItem(allOf(
                    hasProperty("id", equalTo(i.getId())),
                    hasProperty("name", equalTo(i.getName())),
                    hasProperty("description", equalTo(i.getDescription())),
                    hasProperty("available", equalTo(i.getAvailable())),
                    hasProperty("requestId", equalTo(i.getRequestId())),
                    hasProperty("comments", equalTo(i.getComments())),
                    hasProperty("lastBooking", equalTo(i.getLastBooking())),
                    hasProperty("nextBooking", equalTo(i.getNextBooking()))
            )));
        }

        verify(commonService, times(1))
                .getInDBUser(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwner(any(User.class), any(Pageable.class));
    }

    @Test
    void searchItems() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = List.of(item);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                anyString(), anyString(), any(Pageable.class)))
                .thenReturn(items);

        List<ItemDto> itemDtos = List.of(itemDto);
        List<ItemDto> testItemDto = itemService.searchItems("qwerty", user.getId(), 0, 10);
        assertThat(testItemDto, hasSize(itemDtos.size()));
        for (ItemDto i : itemDtos) {
            assertThat(testItemDto, hasItem(allOf(
                    hasProperty("id", equalTo(i.getId())),
                    hasProperty("name", equalTo(i.getName())),
                    hasProperty("description", equalTo(i.getDescription())),
                    hasProperty("available", equalTo(i.getAvailable())),
                    hasProperty("requestId", equalTo(i.getRequestId())),
                    hasProperty("comments", equalTo(i.getComments())),
                    hasProperty("lastBooking", equalTo(i.getLastBooking())),
                    hasProperty("nextBooking", equalTo(i.getNextBooking()))
            )));
        }

        List<ItemDto> testItemDto1 = itemService.searchItems("", user.getId(), 0, 10);
        assertThat(testItemDto1, hasSize(0));

        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(itemRepository, times(1))
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                        anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void addCommentToItem() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getInDbItem(anyLong()))
                .thenReturn(item);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto testCommentDto = itemService.addCommentToItem(item.getId(), user.getId(), commentDto);
        assertThat(testCommentDto.getId(), equalTo(commentDto.getId()));
        assertThat(testCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(testCommentDto.getAuthor(), equalTo(commentDto.getAuthor()));
        assertThat(testCommentDto.getCreated(), equalTo(commentDto.getCreated()));

        CommentDto commentDto1 = commentDto;
        commentDto1.setText("");
        try {
            CommentDto testCommentDto1 = itemService.addCommentToItem(item.getId(), user.getId(), commentDto1);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Text of comment is null or empty"));
        }

        verify(commonService, times(1))
                .getInDBUser(anyLong());
        verify(commonService, times(1))
                .getInDbItem(anyLong());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }
}
