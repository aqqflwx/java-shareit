package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@email.com");
        user = userRepository.save(user);
    }

    @Test
    void createRequest_Success() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Need a drill");

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void getUserRequests_Success() {
        itemRequestService.createRequest(user.getId(), new ItemRequestCreateDto("Request 1"));
        itemRequestService.createRequest(user.getId(), new ItemRequestCreateDto("Request 2"));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllRequests_Success() {
        User anotherUser = new User();
        anotherUser.setName("Another");
        anotherUser.setEmail("another@email.com");
        anotherUser = userRepository.save(anotherUser);

        itemRequestService.createRequest(anotherUser.getId(), new ItemRequestCreateDto("Other request"));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("Other request");
    }

    @Test
    void getRequestById_Success() {
        ItemRequestDto created = itemRequestService.createRequest(user.getId(), new ItemRequestCreateDto("Request"));

        ItemRequestDto result = itemRequestService.getRequestById(user.getId(), created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getDescription()).isEqualTo("Request");
    }
}
