package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingClient bookingClient;
    @Autowired
    private ObjectMapper mapper;

    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now();
        end = LocalDateTime.now();
        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .item(1L)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingClient.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(ResponseEntity.ok(bookingDto));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItem().intValue())));

        verify(bookingClient, times(1))
                .createBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void patchBooking() throws Exception {
        bookingDto.setItem(2L);
        when(bookingClient.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(bookingDto));
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItem().intValue())));

        verify(bookingClient, times(1))
                .patchBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingDto));
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItem().intValue())));

        verify(bookingClient, times(1))
                .getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookingOwnerByStatus() throws Exception {
        List<BookingDto> bookingDtos = List.of(bookingDto);
        when(bookingClient.getBookingOwnerByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(bookingDtos));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtos.size())));

        verify(bookingClient, times(1))
                .getBookingOwnerByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getBookingItemsByStatus() throws Exception {
        List<BookingDto> bookingDtos = List.of(bookingDto);
        when(bookingClient.getBookingItemsByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(bookingDtos));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtos.size())));

        verify(bookingClient, times(1))
                .getBookingItemsByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }
}
