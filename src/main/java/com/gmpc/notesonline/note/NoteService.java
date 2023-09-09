package com.gmpc.notesonline.note;

import com.gmpc.notesonline.note.utils.IdWorker;
import com.gmpc.notesonline.system.exception.ObjectNotFoundException;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final GMPCUserRepository gmpcUserRepository;

    private final IdWorker idWorker;

    public NoteService(NoteRepository noteRepository, GMPCUserRepository gmpcUserRepository, IdWorker idWorker) {
        this.noteRepository = noteRepository;
        this.gmpcUserRepository = gmpcUserRepository;
        this.idWorker = idWorker;
    }

    public Note findById(String noteId){
        return this.noteRepository
                .findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException(Note.class, noteId));
    }

    public List<Note> findAllByOwner_Id(Integer id) {
        List<Note> note = this.noteRepository.findAllByOwner_Id(id);
        if(note == null) throw new UserNotFoundException();
        return note;
    }

    public Note save(Note newNote, String email) {
        GMPCUser gmpcUser = this.gmpcUserRepository.findByEmail(email);
        if(gmpcUser == null) throw new UserNotFoundException(email);
        newNote.setId(String.valueOf(idWorker.nextId()));
        newNote.setTitle(newNote.getTitle() != null ? newNote.getTitle() : "");
        newNote.setDescription(newNote.getDescription() != null ? newNote.getDescription() : "");
        newNote.setDate(new Date());
        newNote.setOwner(gmpcUser);
        return this.noteRepository.save(newNote);
    }

    public void delete(String noteId) {
        this.noteRepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException(Note.class, noteId));
        this.noteRepository.deleteById(noteId);
    }

    public Note update(String noteId, Note note) {
        return this.noteRepository.findById(noteId)
                .map(oldNote -> {
                    if (note.getTitle() != null) {
                        oldNote.setTitle(note.getTitle());
                    }
                    if (note.getDescription() != null) {
                        oldNote.setDescription(note.getDescription());
                    }
                    oldNote.setDate(new Date());
                    return this.noteRepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException(Note.class, noteId));
    }

}
