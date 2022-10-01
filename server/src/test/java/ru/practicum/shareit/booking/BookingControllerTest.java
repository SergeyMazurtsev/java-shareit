package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

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

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private Booking booking;
    private BookingDtoOut bookingDtoOut;
    private BookingDtoIn bookingDtoIn;
    private BookingShortDto bookingShortDto;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        start = LocalDateTime.now().plusMinutes(1);
        end = LocalDateTime.now().plusMinutes(10);
        user = User.builder()
                .id(1L)
                .name("Test name")
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        item = Item.builder()
                .id(1L)
                .name("Test item")
                .owner(user)
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingDtoOut = BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(booking.getStatus())
                .build();
        bookingDtoIn = BookingDtoIn.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getId())
                .build();
        bookingShortDto = BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDtoIn.class), anyLong()))
                .thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .createBooking(any(BookingDtoIn.class), anyLong());
    }

    @Test
    void patchBooking() throws Exception {
        bookingDtoOut.setStatus(BookingStatus.REJECTED);
        when(bookingService.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .patchBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookingOwnerByStatus() throws Exception {
        List<BookingDtoOut> bookingDtoOuts = List.of(bookingDtoOut);
        when(bookingService.getBookingOwnerByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(bookingDtoOuts);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtoOuts.size())))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOuts.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item").exists())
                .andExpect(jsonPath("$[0].booker").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDtoOuts.get(0).getStatus().toString())));

        verify(bookingService, times(1))
                .getBookingOwnerByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    void getBookingItemsByStatus() throws Exception {
        List<BookingDtoOut> bookingDtoOuts = List.of(bookingDtoOut);
        when(bookingService.getBookingItemsByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(bookingDtoOuts);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookingDtoOuts.size())))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOuts.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item").exists())
                .andExpect(jsonPath("$[0].booker").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDtoOuts.get(0).getStatus().toString())));

        verify(bookingService, times(1))
                .getBookingItemsByStatus(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }
}
