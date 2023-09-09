package com.gmpc.notesonline.system;

import com.gmpc.notesonline.note.Note;
import com.gmpc.notesonline.note.NoteRepository;
import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.GMPCUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class DBDataInitializer implements CommandLineRunner {

    public final NoteRepository noteRepository;

    public final GMPCUserRepository gmpcUserRepository;

    public DBDataInitializer(NoteRepository noteRepository, GMPCUserRepository gmpcUserRepository) {
        this.noteRepository = noteRepository;
        this.gmpcUserRepository = gmpcUserRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        GMPCUser user0 = new GMPCUser();
        user0.setId(0);
        user0.setName("name0");
        user0.setEmail("name0@email.com");
        user0.setPassword("1234567890");
        user0.setEnabled(true);
        user0.setRole("user");

        Note note0 = new Note();
        note0.setId("0");
        note0.setTitle("Title0");
        note0.setDescription("Description0");
        note0.setDate(new Date());
        note0.setOwner(user0);

        gmpcUserRepository.save(user0);
    }
}
