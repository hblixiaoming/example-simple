package com.example.vo;
import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = -8275952484393082674L;

    public static final int SUCCESS_CODE = 0;

    private int code;

    private String msgCode;

    private String message;

    private T bean;

    public static final Result<Void> SUCCESSED = success();

    public Result() {

    }

    public Result(int code, String msgCode, String message, T bean) {
        this.code = code;
        this.msgCode = msgCode;
        this.message = message;
        this.bean = bean;
    }

    public Result(int code, String msgCode, String message) {
        this.code = code;
        this.msgCode = msgCode;
        this.message = message;
    }


    public static <T> Result<T> success() {
        return new Result<>(0, "0000", "success");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(-1,"9999", message);
    }
}
