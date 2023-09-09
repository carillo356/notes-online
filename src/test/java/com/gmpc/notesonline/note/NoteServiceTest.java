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
    GMPCUser user0;
    Note note0;
    Note note1;

    @BeforeEach
    void setUp() {
        this.user0 = new GMPCUser();
        user0.setId(0);
        user0.setName("Aaron");
        user0.setEmail("aaron@email.com");
        user0.setPassword("1234567890");
        user0.setEnabled(true);
        user0.setRole("user");

        this.note0 = new Note();
        note0.setId("0");
        note0.setTitle("Title0");
        note0.setDescription("Description0");
        note0.setDate(new Date());
        note0.setOwner(user0);

        this.note1 = new Note();
        note1.setId("1");
        note1.setTitle("Title1");
        note1.setDescription("Description1");
        note1.setDate(new Date());
        note1.setOwner(user0);

        this.notes = new ArrayList<>();
        this.notes.add(note0);
        this.notes.add(note1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        //Given
        given(noteRepository.findById("0")).willReturn(Optional.of(note0));

        //When
        Note returnedNote = noteService.findById("0");

        //Then
        assertThat(returnedNote.getId()).isEqualTo(note0.getId());
        assertThat(returnedNote.getTitle()).isEqualTo(note0.getTitle());
        assertThat(returnedNote.getDescription()).isEqualTo(note0.getDescription());
        assertThat(returnedNote.getDate()).isEqualTo(note0.getDate());
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
        given(noteRepository.findAllByOwner_Id(-1)).willReturn(null);

        //When
        Throwable thrown = catchThrowable(() -> {
            List<Note> actualNotes = noteService.findAllByOwner_Id(-1);
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user");
        verify(noteRepository, times(1)).findAllByOwner_Id(-1);
    }

    @Test
    void testSaveSuccess() {
        //Given
        Note newNote = new Note();
        newNote.setTitle("Amazing Title");
        newNote.setDescription("Amazing Description");
        newNote.setDate(new Date(1));
        newNote.setOwner(user0);

        given(idWorker.nextId()).willReturn(123456L);
        given(noteRepository.save(newNote)).willReturn(newNote);
        given(gmpcUserRepository.findByEmail("aaron@email.com")).willReturn(user0);

        //When
        Note savedNote = noteService.save(newNote, "aaron@email.com");

        //Then
        assertThat(savedNote.getId()).isEqualTo("123456");
        assertThat(savedNote.getTitle()).isEqualTo("Amazing Title");
        assertThat(savedNote.getDescription()).isEqualTo("Amazing Description");
        assertThat(savedNote.getDate()).isEqualTo(newNote.getDate());
        assertThat(savedNote.getOwner()).isEqualTo(user0);
        verify(noteRepository, times(1)).save(newNote);
        verify(gmpcUserRepository, times(1)).findByEmail("aaron@email.com");
    }

    @Test
    void testSaveFailed() {
        //Given
        given(gmpcUserRepository.findByEmail("aaron@email.com")).willReturn(null);
        //When
        Throwable thrown = catchThrowable(()->{
            Note note = noteService.save(note0, "aaron@email.com");
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Could not find user with email aaron@email.com");
        verify(gmpcUserRepository, times(1)).findByEmail("aaron@email.com");
    }

    @Test
    void testDeleteSuccess() {
        //Given
        given(noteRepository.findById("0")).willReturn(Optional.of(note0));
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

        given(noteRepository.findById("0")).willReturn(Optional.of(note0));
        given(noteRepository.save(note0)).willReturn(note0);

        //When
        Note updatedNote = noteService.update("0", update);

        //Then
        assertEquals(updatedNote.getTitle(), update.getTitle());
        assertEquals(updatedNote.getDescription(), update.getDescription());
        assertEquals(updatedNote.getDate(), note0.getDate());
        verify(noteRepository, times(1)).findById("0");
        verify(noteRepository, times(1)).save(note0);

    }
}


