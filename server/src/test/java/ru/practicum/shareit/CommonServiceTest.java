package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CommonServiceTest {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    CommonService commonService = new CommonService(userRepository, itemRepository);

    @Test
    public void getItem() {
        Item item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Testing")
                .available(true)
                .build();
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Item testItem = commonService.getInDbItem(1L);

        assertThat(testItem.getId(), equalTo(item.getId()));
        assertThat(testItem.getName(), equalTo(item.getName()));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    public void getUser() {
        User user = User.builder()
                .id(1L)
                .name("Test")
                .email("qwerty@qqq.ru")
                .build();
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        User testUser = commonService.getInDBUser(1L);

        assertThat(testUser.getId(), equalTo(user.getId()));
        assertThat(testUser.getName(), equalTo(user.getName()));
        assertThat(testUser.getEmail(), equalTo(user.getEmail()));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    public void getItemByRequest() {
        List<Item> items = List.of(
                Item.builder().id(1L).name("Test1").build(),
                Item.builder().id(2L).name("Test2").build());
        Mockito.when(itemRepository.findAllByRequest_Id(anyLong()))
                .thenReturn(items);
        Set<ItemDto> itemDtos = items.stream().map(i -> ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .build()).collect(Collectors.toSet());

        Set<ItemDto> testItems = commonService.getItemsByRequest(1L);
        assertThat(testItems, hasSize(itemDtos.size()));
        for (ItemDto itemDto : itemDtos) {
            assertThat(testItems, hasItem(allOf(
                    hasProperty("id", equalTo(itemDto.getId())),
                    hasProperty("name", equalTo(itemDto.getName()))
            )));
        }
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByRequest_Id(1L);
    }

    @Test
    public void getPagination() {
        Pageable pageableNoSort = PageRequest.of(0, 20);
        Pageable pageableWithSort = PageRequest.of(0, 30, Sort.by("created").descending());

        Pageable testPageableOne = commonService.getPagination(0, 20, null);
        Pageable testPageableTwo = commonService.getPagination(0, 30, "created");

        assertThat(testPageableOne, equalTo(pageableNoSort));
        assertThat(testPageableTwo, equalTo(pageableWithSort));
    }
}
