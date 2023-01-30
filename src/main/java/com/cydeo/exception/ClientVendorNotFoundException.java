package com.cydeo.exception;

public class ClientVendorNotFoundException extends RuntimeException{
    public ClientVendorNotFoundException(String message) {
        super(message);
    }
}
