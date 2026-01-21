package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_Success() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");

        UserDto result = userService.createUser(userDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void updateUser_Success() {
        UserDto created = userService.createUser(new UserDto(null, "Original", "original@email.com"));

        UserDto updateDto = new UserDto(null, "Updated", "updated@email.com");
        UserDto result = userService.updateUser(created.getId(), updateDto);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void getUserById_Success() {
        UserDto created = userService.createUser(new UserDto(null, "Test", "test@email.com"));

        UserDto result = userService.getUserById(created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getName()).isEqualTo("Test");
    }

    @Test
    void getAllUsers_Success() {
        userService.createUser(new UserDto(null, "User1", "user1@email.com"));
        userService.createUser(new UserDto(null, "User2", "user2@email.com"));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteUser_Success() {
        UserDto created = userService.createUser(new UserDto(null, "ToDelete", "delete@email.com"));

        userService.deleteUser(created.getId());

        List<UserDto> allUsers = userService.getAllUsers();
        assertThat(allUsers).noneMatch(u -> u.getId().equals(created.getId()));
    }
}
