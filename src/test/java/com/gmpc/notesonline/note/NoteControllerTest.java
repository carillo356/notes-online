package com.gmpc.notesonline.note;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmpc.notesonline.note.dto.NoteDto;
import com.gmpc.notesonline.system.StatusCode;
import com.gmpc.notesonline.system.exception.ObjectNotFoundException;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserController;
import com.gmpc.notesonline.user.GMPCUserRepository;
import com.gmpc.notesonline.user.GMPCUserService;
import org.h2.engine.User;
import org.hamcrest.Matchers;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
class NoteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    NoteService noteService;
    @MockBean
    GMPCUserRepository gmpcUserRepository;

    @InjectMocks
    NoteController noteController;

    List<Note> notes;

    GMPCUser noteControllerUser0;
    Note noteControllerNote0;
    Note noteControllerNote1;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        this.noteControllerUser0 = new GMPCUser();
        noteControllerUser0.setId(0);
        noteControllerUser0.setName("Aaron");
        noteControllerUser0.setEmail("notecontroller@email.com");
        noteControllerUser0.setPassword("1234567890");
        noteControllerUser0.setEnabled(true);
        noteControllerUser0.setRole("user");

        this.noteControllerNote0 = new Note();
        noteControllerNote0.setId("0");
        noteControllerNote0.setTitle("Title0");
        noteControllerNote0.setDescription("Description0");
        noteControllerNote0.setDate(new Date());
        noteControllerNote0.setOwner(noteControllerUser0);

        this.noteControllerNote1 = new Note();
        noteControllerNote1.setId("1");
        noteControllerNote1.setTitle("Title1");
        noteControllerNote1.setDescription("Description1");
        noteControllerNote1.setDate(new Date());
        noteControllerNote1.setOwner(noteControllerUser0);

        this.notes = new ArrayList<>();
        this.notes.add(noteControllerNote0);
        this.notes.add(noteControllerNote1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindNoteByIdSuccess() throws Exception {
        //Given
        given(this.noteService.findById("0")).willReturn(this.notes.get(0));
        //When and Then
        this.mockMvc.perform(get(this.baseUrl + "/notes/findById/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("0"))
                .andExpect(jsonPath("$.data.title").value("Title0"));
    }

    @Test
    void testFindNoteByIdNotFound() throws Exception {
        //Given
        given(this.noteService.findById("-1")).willThrow(new ObjectNotFoundException(Note.class, "-1"));
        //When and Then
        this.mockMvc.perform(get(this.baseUrl + "/notes/findById/-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Note with Id -1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllNotesSuccess() throws Exception {
        //Given
        given(this.noteService.findAllByOwner_Id(0)).willReturn(notes);

        //When and Then
        this.mockMvc.perform(get(this.baseUrl + "/notes/findAllById/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.notes.size())));
    }

    @Test
    void testFindAllNotesUserNotFound() throws Exception {
        //Given
        given(this.noteService.findAllByOwner_Id(Mockito.any(Integer.class))).willThrow(new UserNotFoundException());

        //When and Then
        this.mockMvc.perform(get(this.baseUrl + "/notes/findAllById/-999").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."));
    }

    @Test
    void testCreateNoteSuccess() throws Exception {
        //Given
        NoteDto noteDto = new NoteDto(null,
                                      "Amazing Title",
                                      "Amazing Description",
                                      null,
                                      null);
        String json = this.objectMapper.writeValueAsString(noteDto);

        Note savedNote = new Note();
        savedNote.setId("123456");
        savedNote.setTitle("Amazing Title");
        savedNote.setDescription("Amazing Description");
        savedNote.setDate(new Date(2023, 9, 6));
        savedNote.setOwner(noteControllerUser0);

        given(this.noteService.save(Mockito.any(Note.class), Mockito.any(String.class))).willReturn(savedNote);

        //When and Then
        this.mockMvc.perform(post(this.baseUrl + "/notes/create/notecontroller@email.com").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.id").value("123456"))
                .andExpect(jsonPath("$.data.title").value("Amazing Title"))
                .andExpect(jsonPath("$.data.description").value("Amazing Description"));
    }

    @Test
    void testCreateNoteUserNotFound() throws Exception {
        //Given
        NoteDto noteDto = new NoteDto(null,
                "Amazing Title",
                "Amazing Description",
                null,
                null);
        String json = this.objectMapper.writeValueAsString(noteDto);

        given(this.noteService.save(Mockito.any(Note.class), Mockito.any(String.class))).willThrow(new UserNotFoundException("test@email.com"));

        //When and Then
        this.mockMvc.perform(post(this.baseUrl + "/notes/create/test@email.com").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."));
    }

    @Test
    void testDeleteNoteSuccess() throws Exception {
        //Given
        doNothing().when(this.noteService).delete(Mockito.any(String.class));

        //When and Then
        this.mockMvc.perform(delete(this.baseUrl + "/notes/delete/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
    }

    @Test
    void testDeleteNoteErrorWithNonExistentId() throws Exception {
        //Given
        doThrow(new ObjectNotFoundException(Note.class, "-1")).when(this.noteService).delete("-1");

        //When and Then
        this.mockMvc.perform(delete(this.baseUrl + "/notes/delete/-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Note with Id -1"));
    }

    @Test
    void testUpdateNoteSuccess() throws Exception {
        //Given
        NoteDto newValuesDto = new NoteDto(null,
                "Amazing Title",
                "Amazing Description",
                null,
                null);
        String json = this.objectMapper.writeValueAsString(newValuesDto);

        Note updateNote = new Note();
        updateNote.setId("0");
        updateNote.setTitle("Amazing Title");
        updateNote.setDescription("Amazing Description");
        updateNote.setDate(new Date());
        updateNote.setOwner(noteControllerUser0);

        given(this.noteService.update(eq("0"), Mockito.any(Note.class))).willReturn(updateNote);

        //When and Then
        this.mockMvc.perform(put(this.baseUrl + "/notes/update/0").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("0"))
                .andExpect(jsonPath("$.data.title").value("Amazing Title"))
                .andExpect(jsonPath("$.data.description").value("Amazing Description"));
    }

    @Test
    void testUpdateNoteErrorWithNonExistentID() throws Exception {
        //Given
        NoteDto newValuesDto = new NoteDto(null,
                "Amazing Title",
                "Amazing Description",
                null,
                null);
        String json = this.objectMapper.writeValueAsString(newValuesDto);
        given(this.noteService.update(eq("-1"), Mockito.any(Note.class))).willThrow(new ObjectNotFoundException(Note.class, "-1"));

        //When and Then
        this.mockMvc.perform(put(this.baseUrl + "/notes/update/-1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find Note with Id -1"));
    }

}