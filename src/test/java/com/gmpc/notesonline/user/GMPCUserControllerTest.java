package com.gmpc.notesonline.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmpc.notesonline.system.StatusCode;
import com.gmpc.notesonline.system.exception.UserAlreadyExist;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import com.gmpc.notesonline.user.converter.GMPCUserToGMPCUserDtoConverter;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GMPCUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @MockBean
    GMPCUserService gmpcUserService;
    @MockBean
    GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter;

    @Autowired
    ObjectMapper objectMapper;

    GMPCUser testUser;

    @BeforeEach
    void setUp() {
        this.testUser = new GMPCUser();
        testUser.setId(0);
        testUser.setName("Carillo");
        testUser.setEmail("carillo@email.com");
        testUser.setPassword("1234567890");
        testUser.setEnabled(true);
        testUser.setRole("user");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        GMPCUser newUser = new GMPCUser();
        newUser.setName("Nocon");
        newUser.setEmail("nocon@email.com");
        newUser.setPassword("1234567890");

        String json = objectMapper.writeValueAsString(newUser);

        GMPCUserDto newUserDto = new GMPCUserDto(0, "Nocon", "nocon@email.com", true, "user", 0);

        //Given
        given(this.gmpcUserService.save(Mockito.any(GMPCUser.class))).willReturn(newUser);
        given(this.gmpcUserToGMPCUserDtoConverter.convert(Mockito.any(GMPCUser.class))).willReturn(newUserDto);

        //When and Then
        this.mockMvc.perform(post(this.baseUrl + "/users/signup").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.id").value(0))
                .andExpect(jsonPath("$.data.name").value("Nocon"))
                .andExpect(jsonPath("$.data.email").value("nocon@email.com"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.role").value("user"))
                .andExpect(jsonPath("$.data.numberOfNotes").value(0));

    }

    @Test
    void testCreateUserAlreadyExist() throws Exception {
        GMPCUser newUser = new GMPCUser();
        newUser.setName("Aaron");
        newUser.setEmail("gmpcusercontroller@email.com");
        newUser.setPassword("1234567890");

        String json = objectMapper.writeValueAsString(newUser);

        GMPCUserDto newUserDto = new GMPCUserDto(0, "Aaron", "gmpcusercontroller@email.com", true, "user", 0);

        //Given
        given(this.gmpcUserService.save(Mockito.any(GMPCUser.class))).willThrow(new UserAlreadyExist("gmpcusercontroller@email.com"));

        //When and Then
        this.mockMvc.perform(post(this.baseUrl + "/users/signup").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("User gmpcusercontroller@email.com already exist."));


    }
}