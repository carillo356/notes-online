package com.gmpc.notesonline.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GMPCUserRepository extends JpaRepository<GMPCUser, Integer> {
    Optional<GMPCUser> findByEmail(String email);

}
