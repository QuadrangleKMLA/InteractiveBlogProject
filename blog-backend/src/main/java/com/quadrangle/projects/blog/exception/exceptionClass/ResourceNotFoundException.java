package com.quadrangle.projects.blog.exception.exceptionClass;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
