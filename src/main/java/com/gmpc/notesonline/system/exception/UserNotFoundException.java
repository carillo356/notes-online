package com.gmpc.notesonline.system.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String email){
        super("Could not find user with email " + email);
    }

    public UserNotFoundException(){ super("Could not find user");

    }
}
