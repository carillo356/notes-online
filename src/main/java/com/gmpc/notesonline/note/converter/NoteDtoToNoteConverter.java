package com.gmpc.notesonline.note.converter;

import com.gmpc.notesonline.note.Note;
import com.gmpc.notesonline.note.dto.NoteDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteDtoToNoteConverter implements Converter<NoteDto, Note> {
    @Override
    public Note convert(NoteDto source) {
        Note note = new Note();
        note.setTitle(source.title());
        note.setDescription(source.description());

        return note;
    }
}
