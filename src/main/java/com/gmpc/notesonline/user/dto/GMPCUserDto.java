package com.gmpc.notesonline.user.dto;

import jakarta.validation.constraints.NotEmpty;

public record GMPCUserDto(Integer id,
                          @NotEmpty(message = "name is required")
                          String name,
                          @NotEmpty(message = "email is required.")
                          String email,
                          boolean enabled,
                          String role,
                          Integer numberOfNotes) {
}
