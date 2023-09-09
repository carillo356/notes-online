package com.gmpc.notesonline.system.exception;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(Class<?> objectType, String id){
        super("Could not find " + objectType.getSimpleName() + " with Id " + id);
    }
    public ObjectNotFoundException(Class<?> objectType, Integer id){
        super("Could not find " + objectType.getSimpleName() + " with Id " + id);
    }
}
