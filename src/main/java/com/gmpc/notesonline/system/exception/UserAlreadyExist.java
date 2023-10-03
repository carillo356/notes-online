package com.gmpc.notesonline.system.exception;

public class UserAlreadyExist extends RuntimeException{
    public UserAlreadyExist(String email){ super("User " + email + " already exist."); }
}
