package com.gmpc.notesonline.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmpc.notesonline.note.dto.NoteDto;
import com.gmpc.notesonline.system.StatusCode;
import com.gmpc.notesonline.system.exception.ObjectNotFoundException;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import org.aspectj.lang.annotation.Before;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Test for Note API endpoints")
@Tag("integration")
public class NoteControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${api.endpoint.base-url}")
    String baseUrl;
    String token;

    @BeforeEach
    void setup() throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(post(this.baseUrl + "/users/login")
                        .with(httpBasic("name0@email.com", "1234567890")));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentString);
        this.token = "Bearer " +json.getJSONObject("data").getString("token");
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testFindNoteByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/notes/findById/-1").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Note with Id -1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testFindAllNotesSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/notes/findAllById/1").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(0)));


    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void testFindAllNotesUserNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/notes/findAllById/-999").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testCreateNoteSuccess() throws Exception {
        NoteDto noteDto = new NoteDto(null,
                "Amazing Title",
                "Amazing Description",
                null,
                null);
        String json = this.objectMapper.writeValueAsString(noteDto);

        this.mockMvc
                .perform(post(this.baseUrl + "/notes/create/name0@email.com")
                    .header("Authorization", this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.title").value("Amazing Title"))
                .andExpect(jsonPath("$.data.description").value("Amazing Description"));
        this.mockMvc.perform(get(this.baseUrl + "/notes/findAllById/1").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void testCreateNoteUserNotFound() throws Exception {
        //Given
        NoteDto noteDto = new NoteDto(null,
                "Amazing Title",
                "Amazing Description",
                null,
                null);
        String json = this.objectMapper.writeValueAsString(noteDto);

        this.mockMvc
                .perform(post(this.baseUrl + "/notes/create/test@email.com")
                    .header("Authorization", this.token)
                    .contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."));
    }

}
