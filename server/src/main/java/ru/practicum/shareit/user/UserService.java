package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto patchUser(UserDto userDto, Long userId);

    void deleteUser(Long userId);

    List<UserDto> getUsers();
}
