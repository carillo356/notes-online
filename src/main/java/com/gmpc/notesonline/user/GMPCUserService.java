package com.gmpc.notesonline.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GMPCUserService {
    private final GMPCUserRepository gmpcUserRepository;
    public GMPCUserService(GMPCUserRepository gmpcUserRepository) {
        this.gmpcUserRepository = gmpcUserRepository;
    }


}
