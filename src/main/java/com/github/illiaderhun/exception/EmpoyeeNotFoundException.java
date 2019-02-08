package com.github.illiaderhun.exception;

public class EmpoyeeNotFoundException extends RuntimeException {

    public EmpoyeeNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
}
