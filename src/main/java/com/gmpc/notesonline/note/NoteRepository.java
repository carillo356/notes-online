package com.gmpc.notesonline.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {
    List<Note> findAllByOwner_Id(Integer id);
}
