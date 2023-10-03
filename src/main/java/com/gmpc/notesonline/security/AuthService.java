package com.gmpc.notesonline.security;

import com.gmpc.notesonline.user.GMPCUser;
import com.gmpc.notesonline.user.MyUserPrincipal;
import com.gmpc.notesonline.user.converter.GMPCUserToGMPCUserDtoConverter;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.gmpcUserToGMPCUserDtoConverter = gmpcUserToGMPCUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        MyUserPrincipal principal =(MyUserPrincipal) authentication.getPrincipal();
        GMPCUser user = principal.getUser();
        GMPCUserDto userDto = gmpcUserToGMPCUserDtoConverter.convert(user);
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;

    }
}
