package com.jim.common;

import java.io.Serializable;

public class ValueResult<ValueType> extends Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private ValueType value;


    public ValueType getValue() {
        return value;
    }

    public void setValue(ValueType value) {
        this.value = value;
    }
}

