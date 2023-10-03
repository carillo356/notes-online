package com.gmpc.notesonline.note;

import com.gmpc.notesonline.note.utils.IdWorker;
import com.gmpc.notesonline.system.exception.ObjectNotFoundException;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        this.gmpcUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        List<Note> note = this.noteRepository.findAllByOwner_Id(id);

        return note;
    }

    public Note save(Note newNote, String email) {
        Optional<GMPCUser> userOptional = this.gmpcUserRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            GMPCUser user = userOptional.get();

            newNote.setId(String.valueOf(idWorker.nextId()));
            newNote.setTitle(newNote.getTitle() != null ? newNote.getTitle() : "");
            newNote.setDescription(newNote.getDescription() != null ? newNote.getDescription() : "");
            newNote.setDate(new Date());
            newNote.setOwner(user);

            return this.noteRepository.save(newNote);
        } else {
            throw new UserNotFoundException(email);
        }
    }

    public void delete(String noteId) {
        Note note = this.noteRepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException(Note.class, noteId));
        note.setOwner(null);
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
