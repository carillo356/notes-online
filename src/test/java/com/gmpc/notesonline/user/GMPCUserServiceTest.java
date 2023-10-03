package com.gmpc.notesonline.user;

import com.gmpc.notesonline.system.exception.UserAlreadyExist;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GMPCUserServiceTest {

    @Mock
    GMPCUserRepository gmpcUserRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    GMPCUserService gmpcUserService;
    GMPCUser user0;

    @BeforeEach
    void setUp() {
        this.user0 = new GMPCUser();
        user0.setId(0);
        user0.setName("Aaron");
        user0.setEmail("aaron@email.com");
        user0.setPassword("1234567890");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveSuccessful() {

        GMPCUser user1 = new GMPCUser();
        user1.setId(1);
        user1.setName("joseph");
        user1.setEmail("joseph@email.com");
        user1.setPassword("123");

        //Given
        given(this.gmpcUserRepository.findByEmail(Mockito.any(String.class))).willReturn(Optional.empty());
        given(this.passwordEncoder.encode(Mockito.any(String.class))).willReturn(new BCryptPasswordEncoder().toString());
        given(this.gmpcUserRepository.save(user1)).willReturn(user1);

        //When
        GMPCUser savedUser = gmpcUserService.save(user1);

        //Then
        assertThat(savedUser.getName()).isEqualTo(user1.getName());
        assertThat(savedUser).isEqualTo(user1);
        assertThat(savedUser.isEnabled()).isEqualTo(true);
        assertThat(savedUser.getRole()).isEqualTo("user");
        verify(gmpcUserRepository, times(1)).findByEmail(Mockito.nullable(String.class));
        verify(gmpcUserRepository, times(1)).save(user1);
    }

    @Test
    void testSaveUserAlreadyExist() {
        //Given
        given(this.gmpcUserRepository.findByEmail(Mockito.any(String.class))).willReturn(Optional.ofNullable(user0));

        //When
        Throwable thrown = catchThrowable(() -> {
            GMPCUser savedUser = gmpcUserService.save(user0);
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessage("User aaron@email.com already exist.");

        verify(gmpcUserRepository, times(1)).findByEmail(Mockito.nullable(String.class));
        verify(gmpcUserRepository, times(0)).save(user0);
    }
}