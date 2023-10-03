package com.gmpc.notesonline.system;

import com.gmpc.notesonline.note.Note;
import com.gmpc.notesonline.note.NoteRepository;
import com.gmpc.notesonline.note.NoteService;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DBDataInitializer implements CommandLineRunner {

    public final NoteRepository noteRepository;
    public final NoteService noteService;
    public final GMPCUserService gmpcUserService;

    public DBDataInitializer(NoteRepository noteRepository, NoteService noteService, GMPCUserService gmpcUserService) {
        this.noteRepository = noteRepository;
        this.noteService = noteService;
        this.gmpcUserService = gmpcUserService;
    }

    @Override
    public void run(String... args) throws Exception {

        GMPCUser user1 = new GMPCUser();
        user1.setId(1);
        user1.setName("name0");
        user1.setEmail("name0@email.com");
        user1.setPassword("1234567890");
        user1.setEnabled(true);
        user1.setRole("user");

        Note note0 = new Note();
        note0.setTitle("Title0");
        note0.setDescription("Description0");
        note0.setOwner(user1);

        gmpcUserService.save(user1);
    }
}
