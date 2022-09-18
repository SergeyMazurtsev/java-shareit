package ru.practicum.shareit.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private Item item;
    private ItemDto itemDto;
    private User user;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        user = User.builder().id(3L).name("User3").build();
        comment = Comment.builder()
                .id(1L)
                .text("Test comments")
                .author(user)
                .build();
        commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getName())
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
    void createItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());

        verify(itemService, times(1))
                .createItem(any(ItemDto.class), anyLong());
    }

    @Test
    void patchItem() throws Exception {
        itemDto.setName("Test2");
        when(itemService.patchItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());

        verify(itemService, times(1))
                .patchItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteItem(anyLong(), anyLong());

        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .deleteItem(anyLong(), anyLong());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());

        verify(itemService, times(1))
                .getItem(anyLong(), anyLong());
    }

    @Test
    void getItemsOfUser() throws Exception {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemService.getItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemDtos);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtos.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtos.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtos.get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].comments").exists())
                .andExpect(jsonPath("$[0].lastBooking").exists())
                .andExpect(jsonPath("$[0].nextBooking").exists());

        verify(itemService, times(1))
                .getItemsOfUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemService.searchItems(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(itemDtos);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("text", "qwerty")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtos.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtos.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtos.get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].comments").exists())
                .andExpect(jsonPath("$[0].lastBooking").exists())
                .andExpect(jsonPath("$[0].nextBooking").exists());

        verify(itemService, times(1))
                .searchItems(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.addCommentToItem(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthor())));

        verify(itemService, times(1))
                .addCommentToItem(anyLong(), anyLong(), any(CommentDto.class));
    }
}
