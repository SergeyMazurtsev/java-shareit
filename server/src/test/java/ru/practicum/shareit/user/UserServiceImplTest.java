package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.exception.ValidatorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);

    private final UserService userService = new UserServiceImpl(userRepository, commonService);
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test")
                .email("qwerty@qqq.ru")
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    public void getUserById() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        UserDto testUserDto = userService.getUserById(1L);
        assertThat(testUserDto.getId(), equalTo(userDto.getId()));
        assertThat(testUserDto.getName(), equalTo(userDto.getName()));
        assertThat(testUserDto.getEmail(), equalTo(userDto.getEmail()));
        verify(commonService, Mockito.times(1))
                .getInDBUser(anyLong());
    }

    @Test
    public void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto testUserDto = userService.createUser(userDto);
        assertThat(testUserDto.getId(), equalTo(userDto.getId()));
        assertThat(testUserDto.getName(), equalTo(userDto.getName()));
        assertThat(testUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto userDto1 = UserDto.builder().name("Test2").email("qqq.ru").build();
        try {
            UserDto testUserDto1 = userService.createUser(userDto1);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), is("Bad request. Email is not valid."));
        }

        UserDto userDto2 = UserDto.builder().email("qqq@qqq.ru").build();
        try {
            UserDto testUserDto1 = userService.createUser(userDto2);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), is("Bad request. Name or email is null."));
        }

        UserDto userDto3 = UserDto.builder().name("").email(user.getEmail()).build();
        try {
            UserDto testUserDto1 = userService.createUser(userDto3);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), is("Bad request. Name or email is empty."));
        }

        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    public void patchUser() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        user.setName("Test2");
        userDto.setName("Test2");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto testUserDto = userService.patchUser(userDto, 1L);
        assertThat(testUserDto.getName(), equalTo(userDto.getName()));
        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
        verify(commonService, Mockito.times(1))
                .getInDBUser(anyLong());
    }

    @Test
    public void deleteUser() {
        Mockito.doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUser(1L);
        verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());
    }

    @Test
    public void getUsers() {
        List<User> users = List.of(
                User.builder().id(1L).name("Test1").build(),
                User.builder().id(2L).name("Test2").build()
        );
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = users.stream().map(i -> UserDto.builder()
                .id(i.getId()).name(i.getName()).build()).collect(Collectors.toList());
        List<UserDto> testUsersDto = userService.getUsers();
        assertThat(testUsersDto, hasSize(usersDto.size()));
        for (UserDto u : usersDto) {
            assertThat(testUsersDto, hasItem(allOf(
                    hasProperty("id", equalTo(u.getId())),
                    hasProperty("name", equalTo(u.getName()))
            )));
        }
        verify(userRepository, Mockito.times(1))
                .findAll();
    }
}
