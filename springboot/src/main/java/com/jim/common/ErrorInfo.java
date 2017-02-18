package com.jim.common;

import java.io.Serializable;

public class ErrorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private String hint;

    public ErrorInfo() {
        this.code = 0;
        this.msg = "";
    }

    public ErrorInfo(ErrorDef errDef) {
        this.setCode(errDef.getCode());
        this.setMsg(errDef.getMsg());
    }

    public ErrorInfo(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }


}
