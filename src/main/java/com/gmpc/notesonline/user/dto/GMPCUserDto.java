package com.gmpc.notesonline.user.dto;

public record GMPCUserDto(Integer id,
                          String name,
                          String email,
                          boolean enabled,
                          String role,
                          Integer numberOfNotes) {
}
