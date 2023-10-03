package com.gmpc.notesonline.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmpc.notesonline.system.StatusCode;
import com.gmpc.notesonline.system.exception.UserAlreadyExist;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Test for User API endpoints")
@Tag("integration")
public class GMPCUserControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testCreateUserSuccess() throws Exception {
        GMPCUser newUser = new GMPCUser();
        newUser.setName("Aaron");
        newUser.setEmail("aaron@email.com");
        newUser.setPassword("1234567890");

        String json = objectMapper.writeValueAsString(newUser);

        this.mockMvc.perform(post(this.baseUrl + "/users/signup").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.name").value("Aaron"))
                .andExpect(jsonPath("$.data.email").value("aaron@email.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.role").value("user"))
                .andExpect(jsonPath("$.data.numberOfNotes").value(0));

    }

    @Test
    void testCreateUserAlreadyExist() throws Exception {
        GMPCUser newUser = new GMPCUser();
        newUser.setName("Aaron");
        newUser.setEmail("aaron@email.com");
        newUser.setPassword("1234567890");

        String json = objectMapper.writeValueAsString(newUser);

        //When and Then
        this.mockMvc.perform(post(this.baseUrl + "/users/signup").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("User aaron@email.com already exist."));
    }

}
