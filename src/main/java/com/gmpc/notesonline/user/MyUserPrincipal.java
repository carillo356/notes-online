package com.gmpc.notesonline.user;

import com.gmpc.notesonline.user.converter.GMPCUserToGMPCUserDtoConverter;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
@Transactional
public class MyUserPrincipal implements UserDetails {

    private GMPCUser gmpcUser;

    public MyUserPrincipal(GMPCUser gmpcUser) {
        this.gmpcUser = gmpcUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(StringUtils.tokenizeToStringArray(this.gmpcUser.getRole(), " "))
                .map(role -> new SimpleGrantedAuthority("Role_" + role))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.gmpcUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.gmpcUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return gmpcUser.isEnabled();
    }

    public GMPCUser getUser() {
        return gmpcUser;
    }

}
