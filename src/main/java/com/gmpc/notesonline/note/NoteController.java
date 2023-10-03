package com.gmpc.notesonline.note;

import com.gmpc.notesonline.note.converter.NoteDtoToNoteConverter;
import com.gmpc.notesonline.note.converter.NoteToNoteDtoConverter;
import com.gmpc.notesonline.note.dto.NoteDto;
import com.gmpc.notesonline.system.Result;
import com.gmpc.notesonline.system.StatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin    
@RequestMapping("${api.endpoint.base-url}/notes")
public class NoteController {

    private final NoteService noteService;

    private final NoteToNoteDtoConverter noteToNoteDtoConverter;

    private final NoteDtoToNoteConverter noteDtoToNoteConverter;

    public NoteController(NoteService noteService, NoteToNoteDtoConverter noteToNoteDtoConverter, NoteDtoToNoteConverter noteDtoToNoteConverter) {
        this.noteService = noteService;
        this.noteToNoteDtoConverter = noteToNoteDtoConverter;
        this.noteDtoToNoteConverter = noteDtoToNoteConverter;
    }

    @GetMapping("/findById/{noteId}")
    public Result findNoteById(@PathVariable String noteId) {
        Note foundNote = this.noteService.findById(noteId);
        NoteDto noteDto = this.noteToNoteDtoConverter.convert(foundNote);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", noteDto );
    }

    @GetMapping("/findAllById/{userId}")
    public Result findAllNotes(@PathVariable Integer userId) {
        List<Note> notes = this.noteService.findAllByOwner_Id(userId);
        List<NoteDto> notesDto = notes.stream()
                .map(noteToNoteDtoConverter::convert)
                .collect(Collectors.toList());

        return new Result(true, StatusCode.SUCCESS, "Find All Success", notesDto);
    }

    @PostMapping("/create/{email}")
    public Result createNote(@RequestBody NoteDto noteDto, @PathVariable String email) {
        Note newNote = noteDtoToNoteConverter.convert(noteDto);
        Note savedNote= this.noteService.save(newNote, email);
        NoteDto savedNoteDto = this.noteToNoteDtoConverter.convert(savedNote);
        return new Result(true, StatusCode.SUCCESS, "Create Success", savedNoteDto);
    }

    @DeleteMapping("/delete/{noteId}")
    public Result deleteNote(@PathVariable String noteId) {
        this.noteService.delete(noteId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success", noteId);
    }

    @PutMapping("/update/{noteId}")
    public Result updateNote(@PathVariable String noteId, @RequestBody NoteDto newValuesDto) {
        Note newValues = this.noteDtoToNoteConverter.convert(newValuesDto);
        Note updatedNote= this.noteService.update(noteId, newValues);
        NoteDto updatedNoteDto = this.noteToNoteDtoConverter.convert(updatedNote);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedNoteDto);
    }
}
