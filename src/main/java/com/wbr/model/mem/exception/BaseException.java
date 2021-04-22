package com.wbr.model.mem.exception;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 7859712770754900356L;

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Exception e) {
        this(e.getMessage());
    }
}