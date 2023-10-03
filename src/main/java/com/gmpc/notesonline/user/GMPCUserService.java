package com.gmpc.notesonline.user;

import com.gmpc.notesonline.system.exception.UserAlreadyExist;
import com.gmpc.notesonline.system.exception.UserNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class GMPCUserService implements UserDetailsService {
    private final GMPCUserRepository gmpcUserRepository;
    private final PasswordEncoder passwordEncoder;


    public GMPCUserService(GMPCUserRepository gmpcUserRepository, PasswordEncoder passwordEncoder) {
        this.gmpcUserRepository = gmpcUserRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public GMPCUser save(GMPCUser gmpcUser) {
        return (GMPCUser) this.gmpcUserRepository.findByEmail(gmpcUser.getEmail())
                .map(existingUser -> {
                    throw new UserAlreadyExist(gmpcUser.getEmail());
                })
                .orElseGet(() -> {
                    gmpcUser.setEnabled(true);
                    gmpcUser.setRole("user");
                    gmpcUser.setPassword(passwordEncoder.encode(gmpcUser.getPassword()));
                    return gmpcUserRepository.save(gmpcUser);
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.gmpcUserRepository.findByEmail(username)
                .map(gmpcUser -> new MyUserPrincipal(gmpcUser))
                .orElseThrow(() -> new UserNotFoundException(username));


    }
}
