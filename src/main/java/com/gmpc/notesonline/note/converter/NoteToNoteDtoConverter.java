package com.gmpc.notesonline.note.converter;

import com.gmpc.notesonline.note.Note;
import com.gmpc.notesonline.note.dto.NoteDto;
import com.gmpc.notesonline.user.converter.GMPCUserToGMPCUserDtoConverter;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteToNoteDtoConverter implements Converter<Note, NoteDto> {

    private final GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter;

    public NoteToNoteDtoConverter(GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter) {
        this.gmpcUserToGMPCUserDtoConverter = gmpcUserToGMPCUserDtoConverter;
    }

    @Override
    public NoteDto convert(Note source) {
        NoteDto noteDto = new NoteDto(source.getId(),
                                        source.getTitle(),
                                        source.getDescription(),
                                        source.getDate(),
                                        source.getOwner() != null
                                                ? this.gmpcUserToGMPCUserDtoConverter.convert(source.getOwner())
                                                : null);

        return noteDto;
    }
}
