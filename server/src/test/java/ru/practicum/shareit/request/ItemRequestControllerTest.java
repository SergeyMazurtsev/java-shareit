package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController requestController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemRequestDtoOut requestDtoOut;
    private ItemRequestDtoIn requestDtoIn;
    private LocalDateTime time;
    private Set<ItemDto> items = Set.of(ItemDto.builder()
            .id(1L).description("Test").available(true).build());

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
        requestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("Test")
                .created(time)
                .items(items).build();
        requestDtoIn = ItemRequestDtoIn.builder()
                .id(requestDtoOut.getId())
                .description(requestDtoOut.getDescription())
                .build();
    }

    @Test
    void createRequest() throws Exception {
        when(requestService.createRequest(anyLong(), any(LocalDateTime.class), any(ItemRequestDtoIn.class)))
                .thenReturn(requestDtoOut);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").exists());
        verify(requestService, times(1))
                .createRequest(anyLong(), any(LocalDateTime.class), any(ItemRequestDtoIn.class));
    }

    @Test
    void getRequests() throws Exception {
        Set<ItemRequestDtoOut> requestDtoOuts = Set.of(requestDtoOut);
        when(requestService.getRequests(anyLong()))
                .thenReturn(requestDtoOuts);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(requestDtoOuts.stream().findFirst().get().getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(requestDtoOuts.stream().findFirst().get().getDescription())))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items").exists());

        verify(requestService, times(1))
                .getRequests(anyLong());
    }

    @Test
    void getRequestsPage() throws Exception {
        Set<ItemRequestDtoOut> requestDtoOuts = Set.of(requestDtoOut);
        when(requestService.getRequestsPage(anyLong(), anyInt(), anyInt()))
                .thenReturn(requestDtoOuts);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(requestDtoOuts.stream().findFirst().get().getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(requestDtoOuts.stream().findFirst().get().getDescription())))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items").exists());

        verify(requestService, times(1))
                .getRequestsPage(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getRequest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDtoOut);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items").exists());

        verify(requestService, times(1))
                .getRequestById(anyLong(), anyLong());
    }
}
