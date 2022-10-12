package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemContoller.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test")
                .description("Testing")
                .available(true)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .text("Testing")
                .author("Test")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(itemClient.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemClient, times(1))
                .createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void patchItem() throws Exception {
        itemDto.setAvailable(false);
        when(itemClient.patchItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemClient, times(1))
                .patchItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void deleteItem() throws Exception {
        when(itemClient.deleteItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .deleteItem(anyLong(), anyLong());
    }

    @Test
    void getItem() throws Exception {
        when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDto));
        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemClient, times(1))
                .getItem(anyLong(), anyLong());
    }

    @Test
    void getItemsOfUser() throws Exception {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemClient.getItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(itemDtos));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(itemDtos.size())));

        verify(itemClient, times(1))
                .getItemsOfUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemClient.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(itemDtos));
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "qwerty")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemClient, times(1))
                .searchItems(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemClient.addCommentToItem(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(commentDto));
        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthor())))
                .andExpect(jsonPath("$.created").exists());

        verify(itemClient, times(1))
                .addCommentToItem(anyLong(), anyLong(), any(CommentDto.class));
    }
}
