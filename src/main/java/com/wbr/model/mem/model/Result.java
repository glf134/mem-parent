package com.wbr.model.mem.model;

import java.io.Serializable;

/**
 * θΏεη»ζ
 *
 * @author pxs
 */
public class Result<T> implements Serializable {

    private static final Long serialVersionUID = 1L;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.setCode("200");
        this.data = data;
    }
    private String message;

    private T data;
}
