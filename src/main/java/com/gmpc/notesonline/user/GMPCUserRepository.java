package com.gmpc.notesonline.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GMPCUserRepository extends JpaRepository<GMPCUser, Integer> {
    GMPCUser findByEmail(String email);
}
