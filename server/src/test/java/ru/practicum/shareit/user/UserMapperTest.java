package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

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
    public void toUserDto() {
        UserDto testUserDto = UserMapper.toUserDto(user);

        assertThat(testUserDto.getId(), equalTo(userDto.getId()));
        assertThat(testUserDto.getName(), equalTo(userDto.getName()));
        assertThat(testUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void toUser() {
        User testUser = UserMapper.toUser(userDto);

        assertThat(testUser.getId(), equalTo(user.getId()));
        assertThat(testUser.getName(), equalTo(user.getName()));
        assertThat(testUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void patchUser() {
        userDto.setName("Test2");
        userDto.setEmail("qwerty2@www.ru");

        UserMapper.patchUser(userDto, user);
        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}
