package com.jim.framework.web.common;

public enum ErrorDef {

    /**
     * OK
     */
    OK(000000, "OK"),

    /**
     * 参数校验错误
     */
    InvalidParameters(1001, "参数校验错误"),

    /**
     * 服务器错误
     */
    ServerError(1003, "服务器错误");

    private String msg;
    private int code;

    private ErrorDef(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public static String getName(int index) {
        for (ErrorDef c : ErrorDef.values()) {
            if (c.getCode() == index) {
                return c.msg;
            }
        }
        return null;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

}
