package com.gmpc.notesonline.user;

import com.gmpc.notesonline.system.Result;
import com.gmpc.notesonline.system.StatusCode;
import com.gmpc.notesonline.user.converter.GMPCUserToGMPCUserDtoConverter;
import com.gmpc.notesonline.user.dto.GMPCUserDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("${api.endpoint.base-url}/users")
public class GMPCUserController {

    private final GMPCUserService gmpcUserService;
    private final GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter;

    public GMPCUserController(GMPCUserService gmpcUserService, GMPCUserToGMPCUserDtoConverter gmpcUserToGMPCUserDtoConverter) {
        this.gmpcUserService = gmpcUserService;
        this.gmpcUserToGMPCUserDtoConverter = gmpcUserToGMPCUserDtoConverter;
    }

    @PostMapping("/signup")
    public Result createUser(@Valid @RequestBody GMPCUser newGMPCUser) {
        GMPCUser newUser = gmpcUserService.save(newGMPCUser);
        GMPCUserDto newUserDto = gmpcUserToGMPCUserDtoConverter.convert(newUser);
        return new Result(true, StatusCode.SUCCESS, "Create Success", newUserDto);
    }


}
