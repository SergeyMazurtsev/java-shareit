package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test1")
                .email("qwerty@qqq.ru")
                .build();
    }

    @Test
    public void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        mvc.perform(get("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .getUserById(anyLong());
    }

    @Test
    public void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .createUser(any(UserDto.class));
    }

    @Test
    public void patchUser() throws Exception {
        userDto.setName("Test2");
        when(userService.patchUser(any(UserDto.class), anyLong()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .patchUser(any(UserDto.class), anyLong());
    }

    @Test
    public void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());
        mvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteUser(anyLong());
    }

    @Test
    void getUsers() throws Exception {
        List<UserDto> userDtos = List.of(
                UserDto.builder().id(1L).name("Test1").email("qwerty@qqq.tu").build(),
                UserDto.builder().id(2L).name("Test2").email("qwerty@www.tu").build()
        );
        when(userService.getUsers())
                .thenReturn(userDtos);
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtos.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(userDtos.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDtos.get(1).getName())))
                .andExpect(jsonPath("$[1].email", is(userDtos.get(1).getEmail())));
        verify(userService, times(1))
                .getUsers();
    }
}
