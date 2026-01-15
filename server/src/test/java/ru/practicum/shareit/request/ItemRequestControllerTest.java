package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createRequest_Success() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.now(), List.of());
        when(itemRequestService.createRequest(eq(1L), any(ItemRequestCreateDto.class))).thenReturn(dto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemRequestCreateDto("Need a drill"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getUserRequests_Success() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Request", LocalDateTime.now(), List.of());
        when(itemRequestService.getUserRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequests_Success() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Other request", LocalDateTime.now(), List.of());
        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById_Success() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "Request", LocalDateTime.now(), List.of());
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Request"));
    }
}
