package com.example.uipractice.net;

import android.text.TextUtils;

/**
 * @date 2020-01-05
 * @Author luffy
 * @description
 */
public class ServiceException extends Exception {
    private int code;
    private String message;
    private Throwable rawThrowable;//原始错误

    private ServiceException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (TextUtils.isEmpty(message)) {
            return "未知错误";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getRawThrowable() {
        return rawThrowable;
    }

    public void setRawThrowable(Throwable rawThrowable) {
        this.rawThrowable = rawThrowable;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
