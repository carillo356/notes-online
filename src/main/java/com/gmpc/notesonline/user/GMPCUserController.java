package com.gmpc.notesonline.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class GMPCUserController {

    GMPCUserService gmpcUserService;

    public GMPCUserController(GMPCUserService gmpcUserService) {
        this.gmpcUserService = gmpcUserService;
    }


}
