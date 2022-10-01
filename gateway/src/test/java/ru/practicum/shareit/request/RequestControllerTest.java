package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private RequestController requestController;
    @Autowired
    private ObjectMapper mapper;

    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RequestDto.builder()
                .id(1L)
                .description("Testing")
                .build();
    }

    @Test
    void createRequest() throws Exception {
        when(requestController.createRequest(anyLong(), any(RequestDto.class)))
                .thenReturn(ResponseEntity.ok(requestDto));
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestController, times(1))
                .createRequest(anyLong(), any(RequestDto.class));
    }

    @Test
    void getRequests() throws Exception {
        Set<RequestDto> requestDtos = Set.of(requestDto);
        when(requestController.getRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(requestDtos));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requestDtos.size())))
                .andExpect(jsonPath("$[0].id",
                        is(requestDtos.stream().findFirst().get().getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(requestDtos.stream().findFirst().get().getDescription())));

        verify(requestController, times(1))
                .getRequests(anyLong());
    }

    @Test
    void getRequestsPage() throws Exception {
        Set<RequestDto> requestDtos = Set.of(requestDto);
        when(requestController.getRequestsPage(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(requestDtos));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requestDtos.size())))
                .andExpect(jsonPath("$[0].id",
                        is(requestDtos.stream().findFirst().get().getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(requestDtos.stream().findFirst().get().getDescription())));

        verify(requestController, times(1))
                .getRequestsPage(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getRequest() throws Exception {
        when(requestController.getRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(requestDto));
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(requestController, times(1))
                .getRequest(anyLong(), anyLong());
    }
}
