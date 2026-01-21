package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createItem_Success() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null);
        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void updateItem_Success() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Updated", "Updated desc", true, null, null);
        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getItemById_Success() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        when(itemService.getItemById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getItemsByOwner_Success() throws Exception {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(1L);
        dto.setName("Item");
        when(itemService.getItemsByOwner(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchItems_Success() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, null, null);
        when(itemService.searchItems("drill")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment_Success() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great!", "Author", LocalDateTime.now());
        when(itemService.addComment(eq(1L), eq(1L), any(CommentCreateDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateDto("Great!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great!"));
    }
}
