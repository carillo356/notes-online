package com.gmpc.notesonline.note;

import com.gmpc.notesonline.note.utils.IdWorker;
import com.gmpc.notesonline.system.exception.ObjectNotFoundException;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @Mock
    GMPCUserRepository gmpcUserRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    NoteService noteService;

    List<Note> notes;
    GMPCUser noteServiceUser0;
    Note noteServiceNote0;
    Note noteServiceNote1;

    @BeforeEach
    void setUp() {
        this.noteServiceUser0 = new GMPCUser();
        noteServiceUser0.setId(0);
        noteServiceUser0.setName("Aaron");
        noteServiceUser0.setEmail("noteservice@email.com");
        noteServiceUser0.setPassword("1234567890");
        noteServiceUser0.setEnabled(true);
        noteServiceUser0.setRole("user");

        this.noteServiceNote0 = new Note();
        noteServiceNote0.setId("0");
        noteServiceNote0.setTitle("Title0");
        noteServiceNote0.setDescription("Description0");
        noteServiceNote0.setDate(new Date());
        noteServiceNote0.setOwner(noteServiceUser0);

        this.noteServiceNote1 = new Note();
        noteServiceNote1.setId("1");
        noteServiceNote1.setTitle("Title1");
        noteServiceNote1.setDescription("Description1");
        noteServiceNote1.setDate(new Date());
        noteServiceNote1.setOwner(noteServiceUser0);

        this.notes = new ArrayList<>();
        this.notes.add(noteServiceNote0);
        this.notes.add(noteServiceNote1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        //Given
        given(noteRepository.findById("0")).willReturn(Optional.of(noteServiceNote0));

        //When
        Note returnedNote = noteService.findById("0");

        //Then
        assertThat(returnedNote.getId()).isEqualTo(noteServiceNote0.getId());
        assertThat(returnedNote.getTitle()).isEqualTo(noteServiceNote0.getTitle());
        assertThat(returnedNote.getDescription()).isEqualTo(noteServiceNote0.getDescription());
        assertThat(returnedNote.getDate()).isEqualTo(noteServiceNote0.getDate());
        verify(noteRepository, times(1)).findById("0");
    }

    @Test
    void testFindByIdNotFound() {
        //Given
        given(noteRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());
        //When
        Throwable thrown = catchThrowable(()->{
            Note returnedNote = noteService.findById("0");
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Note with Id 0");
        verify(noteRepository, times(1)).findById("0");
    }

    @Test
    void testFindAllByOwner_Id_Success() {
        //Given
        given(gmpcUserRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.ofNullable(noteServiceUser0));
        given(noteRepository.findAllByOwner_Id(0)).willReturn(notes);

        //When
        List<Note> actualNotes = noteService.findAllByOwner_Id(0);

        //Then
        assertThat(actualNotes.size()).isEqualTo(this.notes.size());
        verify(noteRepository, times(1)).findAllByOwner_Id(0);
    }

    @Test
    void testFindAllByOwner_Id_NotFound() {
        //Given
        given(gmpcUserRepository.findById(-1)).willThrow(new UserNotFoundException());

        //When
        Throwable thrown = catchThrowable(() -> {
            List<Note> actualNotes = noteService.findAllByOwner_Id(-1);
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user");
        verify(gmpcUserRepository, times(1)).findById(-1);
    }

    @Test
    void testSaveSuccess() {
        //Given
        Note newNote = new Note();
        newNote.setTitle("Amazing Title");
        newNote.setDescription("Amazing Description");
        newNote.setDate(new Date(1));
        newNote.setOwner(noteServiceUser0);

        given(idWorker.nextId()).willReturn(123456L);
        given(noteRepository.save(newNote)).willReturn(newNote);
        given(gmpcUserRepository.findByEmail("noteservice@email.com")).willReturn(Optional.ofNullable(noteServiceUser0));

        //When
        Note savedNote = noteService.save(newNote, "noteservice@email.com");

        //Then
        assertThat(savedNote.getId()).isEqualTo("123456");
        assertThat(savedNote.getTitle()).isEqualTo("Amazing Title");
        assertThat(savedNote.getDescription()).isEqualTo("Amazing Description");
        assertThat(savedNote.getDate()).isEqualTo(newNote.getDate());
        assertThat(savedNote.getOwner()).isEqualTo(noteServiceUser0);
        verify(noteRepository, times(1)).save(newNote);
        verify(gmpcUserRepository, times(1)).findByEmail("noteservice@email.com");
    }

    @Test
    void testSaveFailed() {
        //Given
        given(gmpcUserRepository.findByEmail("noteservice@email.com")).willReturn(Optional.ofNullable(Mockito.any(GMPCUser.class)));
        //When
        Throwable thrown = catchThrowable(()->{
            Note note = noteService.save(noteServiceNote0, "noteservice@email.com");
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user with email noteservice@email.com");
        verify(gmpcUserRepository, times(1)).findByEmail("noteservice@email.com");
    }

    @Test
    void testDeleteSuccess() {
        //Given
        given(noteRepository.findById("0")).willReturn(Optional.of(noteServiceNote0));
        doNothing().when(noteRepository).deleteById("0");
        //When
        noteService.delete("0");

        //Then
        verify(noteRepository, times(1)).findById("0");
        verify(noteRepository, times(1)).deleteById("0");

    }

    @Test
    void testDeleteNotFound() {
        //Given
        given(noteRepository.findById("0")).willReturn(Optional.empty());
        //When
        Throwable thrown = catchThrowable(() -> {
            noteService.delete("0");
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Note with Id 0");
        verify(noteRepository, times(1)).findById("0");
        verify(noteRepository, times(0)).deleteById("0");

    }

    @Test
    void testUpdateSuccess() {
        //Given
        Note update  = new Note();
        update.setTitle("New Title");
        update.setDescription("New Description");
        update.setDate(new Date());

        given(noteRepository.findById("0")).willReturn(Optional.of(noteServiceNote0));
        given(noteRepository.save(noteServiceNote0)).willReturn(noteServiceNote0);

        //When
        Note updatedNote = noteService.update("0", update);

        //Then
        assertEquals(updatedNote.getTitle(), update.getTitle());
        assertEquals(updatedNote.getDescription(), update.getDescription());
        assertEquals(updatedNote.getDate(), noteServiceNote0.getDate());
        verify(noteRepository, times(1)).findById("0");
        verify(noteRepository, times(1)).save(noteServiceNote0);

    }
}


