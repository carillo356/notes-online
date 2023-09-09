package com.gmpc.notesonline.note.dto;

import com.gmpc.notesonline.user.dto.GMPCUserDto;

import java.util.Date;

public record NoteDto(String id,
                      String title,
                      String description,
                      Date date,
                      GMPCUserDto owner) {
}
