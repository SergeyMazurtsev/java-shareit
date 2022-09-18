package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
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
public class ItemRequestServiceImplTest {
    private final ItemRequestRepository requestRepository = Mockito.mock(ItemRequestRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);

    private final ItemRequestService requestService = new ItemRequestServiceImpl(requestRepository, commonService);

    private User user;
    private ItemRequestDtoIn requestDtoIn;
    private ItemRequestDtoOut requestDtoOut;
    private ItemRequest request;
    private Set<ItemDto> items = Set.of(ItemDto.builder()
            .id(1L).description("Test").available(true).build());
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        user = User.builder()
                .id(1L)
                .name("Test")
                .email("qwerty@qq.ru").build();
        requestDtoIn = ItemRequestDtoIn.builder()
                .id(1L)
                .description("Testing").build();
        requestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("Testing")
                .created(time)
                .items(items).build();
        request = ItemRequest.builder()
                .id(1L)
                .description("Testing")
                .created(time)
                .requestor(user).build();
    }

    @Test
    void createRequest() {
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getItemsByRequest(anyLong()))
                .thenReturn(items);
        ItemRequestDtoOut testRequestOut = requestService.createRequest(1L, time, requestDtoIn);
        assertThat(testRequestOut.getId(), equalTo(requestDtoOut.getId()));
        assertThat(testRequestOut.getDescription(), equalTo(requestDtoOut.getDescription()));
        assertThat(testRequestOut.getCreated(), equalTo(requestDtoOut.getCreated()));
        assertThat(testRequestOut.getItems(), equalTo(requestDtoOut.getItems()));

        ItemRequestDtoIn requestDtoIn1 = ItemRequestDtoIn.builder()
                .id(1L).build();
        try {
            ItemRequestDtoOut testRequestOut1 = requestService.createRequest(1L, time, requestDtoIn1);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request."));
        }

        ItemRequestDtoIn requestDtoIn2 = ItemRequestDtoIn.builder()
                .id(1L).description("").build();
        try {
            ItemRequestDtoOut testRequestOut1 = requestService.createRequest(1L, time, requestDtoIn2);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request."));
        }

        verify(requestRepository, times(1))
                .save(any(ItemRequest.class));
        verify(commonService, times(3))
                .getInDBUser(anyLong());
        verify(commonService, times(1))
                .getItemsByRequest(anyLong());
    }

    @Test
    void getRequests() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(requestRepository.findAllByRequestor(any(User.class)))
                .thenReturn(Set.of(request));
        when(commonService.getItemsByRequest(anyLong()))
                .thenReturn(items);

        Set<ItemRequestDtoOut> requestDtoOuts = Set.of(requestDtoOut);
        Set<ItemRequestDtoOut> testRequestOut = requestService.getRequests(1L);
        assertThat(testRequestOut, hasSize(requestDtoOuts.size()));
        for (ItemRequestDtoOut r : requestDtoOuts) {
            assertThat(testRequestOut, hasItem(allOf(
                    hasProperty("id", equalTo(r.getId())),
                    hasProperty("description", equalTo(r.getDescription())),
                    hasProperty("created", equalTo(r.getCreated())),
                    hasProperty("items", equalTo(r.getItems()))
            )));
        }

        verify(requestRepository, times(1))
                .findAllByRequestor(any(User.class));
        verify(commonService, times(1))
                .getInDBUser(anyLong());
        verify(commonService, times(1))
                .getItemsByRequest(anyLong());
    }

    @Test
    void getRequestsPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created"));
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(requestRepository.findByRequestorNot(any(User.class), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(commonService.getPagination(anyInt(), anyInt(), anyString()))
                .thenReturn(pageable);
        when(commonService.getItemsByRequest(anyLong()))
                .thenReturn(items);

        Set<ItemRequestDtoOut> requestDtoOuts = Set.of(requestDtoOut);
        Set<ItemRequestDtoOut> testRequestOut = requestService.getRequestsPage(1L, 0, 10);
        assertThat(testRequestOut, hasSize(requestDtoOuts.size()));
        for (ItemRequestDtoOut r : requestDtoOuts) {
            assertThat(testRequestOut, hasItem(allOf(
                    hasProperty("id", equalTo(r.getId())),
                    hasProperty("description", equalTo(r.getDescription())),
                    hasProperty("created", equalTo(r.getCreated())),
                    hasProperty("items", equalTo(r.getItems()))
            )));
        }

        verify(requestRepository, times(1))
                .findByRequestorNot(any(User.class), any(Pageable.class));
        verify(commonService, times(1))
                .getInDBUser(anyLong());
        verify(commonService, times(1))
                .getItemsByRequest(anyLong());
        verify(commonService,times(1))
                .getPagination(anyInt(), anyInt(), any());
    }

    @Test
    void getRequestById() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));
        when(commonService.getItemsByRequest(anyLong()))
                .thenReturn(items);

        ItemRequestDtoOut testRequestOut = requestService.getRequestById(1L, 1L);
        assertThat(testRequestOut.getId(), equalTo(requestDtoOut.getId()));
        assertThat(testRequestOut.getDescription(), equalTo(requestDtoOut.getDescription()));
        assertThat(testRequestOut.getCreated(), equalTo(requestDtoOut.getCreated()));
        assertThat(testRequestOut.getItems(), equalTo(requestDtoOut.getItems()));

        verify(commonService, times(1))
                .getInDBUser(anyLong());
        verify(requestRepository, times(1))
                .findById(anyLong());
        verify(commonService, times(1))
                .getItemsByRequest(anyLong());
    }
}
