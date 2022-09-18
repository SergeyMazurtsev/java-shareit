package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.exception.IdViolationException;
import ru.practicum.shareit.exception.ValidatorException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailValidator validator = EmailValidator.getInstance();
    private final CommonService commonService;


    @Override
    public UserDto getUserById(Long userId) {
        User user = commonService.getInDBUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (!validator.isValid(user.getEmail())) {
            throw new ValidatorException("Bad email.");
        }
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidatorException("Bad request.");
        }
        if (user.getName().isEmpty() || user.getEmail().isEmpty()) {
            throw new ValidatorException("Bad request.");
        }
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("User already in base.");
        }
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long userId) {
        User user = commonService.getInDBUser(userId);
        UserMapper.patchUser(userDto, user);
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("Already have user like this.");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidatorException("Bad request.");
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}