package com.gmpc.notesonline.user.converter;

import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GMPCUserToGMPCUserDtoConverter implements Converter<GMPCUser, GMPCUserDto> {
    @Override
    public GMPCUserDto convert(GMPCUser source) {
        GMPCUserDto gmpcUserDto = new GMPCUserDto(source.getId(),
                                                  source.getName(),
                                                  source.getEmail(),
                                                  source.isEnabled(),
                                                  source.getRole(),
                                                  source.getNumberOfNotes());
        return gmpcUserDto;
    }
}
