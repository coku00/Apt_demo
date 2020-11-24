package com.coku.lib;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/20.
 * @email coku_lwp@126.com
 */
public class TError extends Throwable{
    String code;
    String message;
    public TError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "TError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
