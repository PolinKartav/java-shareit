package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private static CreateUpdateItemRequestDto correctRequest;
    private static CreateUpdateItemRequestDto requestWithBlankDescription;
    private static CreateUpdateItemRequestDto requestWithDescriptionSize1001;
    private static ItemRequestDto getItemRequestDto;
    private static List<ItemRequestDto> listOfRequests;

    @BeforeAll
    static void beforeAll() {
        correctRequest = CreateUpdateItemRequestDto.builder()
                .description("description")
                .build();

        requestWithBlankDescription = CreateUpdateItemRequestDto.builder()
                .description(" ")
                .build();

        requestWithDescriptionSize1001 = CreateUpdateItemRequestDto.builder()
                .description("A".repeat(1001))
                .build();

        getItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .build();

        listOfRequests = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfRequests.add(getItemRequestDto.toBuilder().id(i + 1L).build());
        }
    }

    @Test
    void shouldExceptionWithCreateRequestWithoutHeader() throws Exception {
        when(requestService.add(any(CreateUpdateItemRequestDto.class), anyLong()))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(correctRequest);

        mockMvc.perform(post("/requests")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).add(any(CreateUpdateItemRequestDto.class), anyLong());
    }

    @Test
    void shouldExceptionWithCreateRequestWithRequestWithBlankDescription() throws Exception {
        when(requestService.add(any(CreateUpdateItemRequestDto.class), anyLong()))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(requestWithBlankDescription);

        mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).add(any(CreateUpdateItemRequestDto.class), anyLong());
    }

    @Test
    void shouldExceptionWithCreateRequestWithRequestWithDescriptionSize1001() throws Exception {
        when(requestService.add(any(CreateUpdateItemRequestDto.class), anyLong()))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(requestWithDescriptionSize1001);

        mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).add(any(CreateUpdateItemRequestDto.class), anyLong());
    }

    @Test
    void shouldCreateRequest() throws Exception {
        when(requestService.add(any(CreateUpdateItemRequestDto.class), anyLong()))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(correctRequest);

        mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(getItemRequestDto.getDescription()));
        verify(requestService, times(1)).add(any(CreateUpdateItemRequestDto.class), anyLong());
    }

    @Test
    void shouldExceptionWithGetAllRequestsByUserIdWithRequestWithoutHeader() throws Exception {
        when(requestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).getUserRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllRequestsByUserId() throws Exception {
        when(requestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(requestService, times(1)).getUserRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldExceptionWithGetAllRequestsWithRequestWithoutHeader() throws Exception {
        when(requestService.getUserRequestsById(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).getUserRequestsById(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldExceptionWithGetAllRequestsWithFromLessThen0() throws Exception {
        when(requestService.getUserRequestsById(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests/all?from=-1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).getUserRequestsById(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldExceptionWithGetAllRequestsWithSizeLessThen1() throws Exception {
        when(requestService.getUserRequestsById(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests/all?size=0")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).getUserRequestsById(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        when(requestService.getUserRequestsById(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests/all")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(requestService, times(1)).getUserRequestsById(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldExceptionWithGetRequestByIdWithRequestWithoutHeader() throws Exception {
        when(requestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(getItemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).getItemRequestById(anyLong(), anyLong());
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(requestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(getItemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(getItemRequestDto.getDescription()));
        verify(requestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }
}
